// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
var _ = require('underscore');
Parse.Cloud.define("hello", function(request, response) {
	response.success("Hello world!");
});

Parse.Cloud.define("destroyFriendship", function(request, response) {
	Parse.Cloud.useMasterKey();
	var fromId = request.params.fromId;
	var toId = request.params.toId;
	
	// generate the two queries
	var q1 = new Parse.Query(Parse.User);
	var q2 = new Parse.Query(Parse.User);
	
	// execute both queries in parallel. When they complete...
	Parse.Promise.when([q1.get(fromId), q2.get(toId)]).then(function(from, to) {
		console.log("from: " + from);
		console.log("to: " + to);
		var fromFriends = from.get('friends');
		var toFriends = to.get('friends');
		var fromFriendIds = _.map(fromFriends, function(friend) {
			return friend.id;
		})
		var toFriendIds = _.map(toFriends, function(friend) {
			return friend.id;
		})
		if (fromFriendIds.indexOf(to.id) < 0) {
			console.log(to.id + " is not a friend of " + from.id);
		} else {
			console.log(to.id + " is a friend of " + from.id + ". Removing.");
			fromFriends.splice(fromFriendIds.indexOf(to.id), 1);
		}
		if (toFriendIds.indexOf(from.id) < 0) {
			console.log(from.id + " is not a friend of " + to.id);
		} else {
			console.log(from.id + " is a friend of " + to.id + ". Removing.");
			toFriends.splice(toFriendIds.indexOf(from.id), 1);
		}
		return Parse.Object.saveAll([from, to]);
	}).then(function() {
		response.success("Saved.");
	}, function(error) {
		response.error(error);
	})
	
})

Parse.Cloud.define("addNewEvent", function(request, response) {
	Parse.Cloud.useMasterKey();
	var Event = Parse.Object.extend("Event");
	var query = new Parse.Query(Event);
	var eventId = request.params.eventId;
	query.get(eventId).then(function(event) {
		console.log("Event is " + event);
		var relation = event.relation("streamFriends");
		q2 = relation.query();
		q2.find().then(function (streamFriends) {
			console.log("streamFriends: " + streamFriends);
			for (var i = 0; i < streamFriends.length; i++) {
				var userEvents = streamFriends[i].get("events");
				var hasEvent = false;
				for (var k = 0; k < userEvents.length; k++) {
					if (userEvents[k].id == eventId){ 
						hasEvent = true;
						break;
					}
					
				}
				if (!hasEvent) {
					console.log("Persisting.");
					userEvents.push(event);
				}
			}
			Parse.Object.saveAll(streamFriends).then(function() {
				response.success("yay.");
			}, function(error) {
				response.error(error);
			})
		}, function (error) {
			response.error(error);
		})
	}, function(error) {
		response.error(error);
	})
})

Parse.Cloud.job("cleanAllEvents", function(request, response) {
	Parse.Cloud.useMasterKey();
	var Event = Parse.Object.extend("Event");
	var query = new Parse.Query(Event);
	query.find().then(function(events) {
		// look for events more than 24 hours old
		var toDelete = []
		var now = new Date().getTime() / 1000;
		var targetTime = now - 3600 * 24;
		for (var i = 0; i < events.length; i++) {
			var event = events[i];
			if (!event.get("pushed")) {
				toDelete.push(event);
			} else if (event.get("eventTime").getTime() < targetTime) {
				toDelete.push(event);
			}
		}
		
	console.log("toDelete.length: " + toDelete.length);
	return toDelete;
	}).then(function(eventsToDelete) {
		var q = new Parse.Query(Parse.User);
		var toPersist = [];
		q.include('events');
		q.find().then(function(allUsers) {
			allUsers.forEach(function(user) {
				var events = user.get('events');
				var needPersist = false;
				for (var i = events.length - 1; i >= 0; i--) {
					if (events[i] == null) {
						console.log("Found a null event for " + user.id);
						events.splice(i,1);
						needPersist = true;
					} else {
						for (var k = 0; k < eventsToDelete.length; k++) {
							if (events[i].id == eventsToDelete[k].id) {
								console.log("Found event " + events[i].id + " for user " + user.id);
								events.splice(i,1);
								needPersist = true;
								break;
							}
						}
					}
				}
				if (needPersist) {
					toPersist.push(user);
				}
			})
		return [toPersist, eventsToDelete];
		}).then(function(data){
			var toPersist = data[0];
			var toDelete = data[1];
			console.log("toPersist.length: " + toPersist.length);
			var promise = Parse.Promise.as();
			promise = promise.then(function() {
				return Parse.Object.saveAll(toPersist);
			});
			promise = promise.then(function() {
				console.log("deleting events.");
				return Parse.Object.destroyAll(toDelete);
			});
			return promise;
		}).then(function() {
			console.log("Done.");
			response.success("Booyah.");
		})
	})
})

Parse.Cloud.define("eventCleanup", function(request, response) {
	Parse.Cloud.useMasterKey();
	var Event = Parse.Object.extend("Event");
	var query = new Parse.Query(Event);
	var eventId = request.params.eventId;
	query.get(eventId, {
		success : function(event) {
			var innerQuery = new Parse.Query(Parse.User);
			// users whose events array contain the event in question
			innerQuery.equalTo("events", event)
			innerQuery.find({
				success : function(users) {
					for (var j = 0; j < users.length; j++) {
						user = users[j];
						var events = user.get("events");
						for (var i = events.length - 1; i >= 0; i--) {
							if (events[i].id == eventId) {
								// remove the event from their array
								console.log("Removing an object because " + events[i].id + " == " + eventId);
								events.splice(i, 1);
							}
						}
						console.log("events after removal: " + events);
					}
					// persist updated users
					Parse.Object.saveAll(users, {
						success : function(list) {
							// destroy the event
							event.destroy({
								success : function() {
									response.success("Yay.");

								},
								error : function(error) {
									response.error(error);
								}
							})

						},
						error : function(error) {
							response.error(error);

						},

					});

				},
				error : function(error) {
					reseponse.error(error)
				}
			})
		},
		error : function(object, error) {
			console.log("Query unsuccessful.");
			response.error(error);
		}
	});
});

Parse.Cloud.define("acceptFriendRequest", function(request, response) {
	Parse.Cloud.useMasterKey();
	var fromId = request.params.fromId;
	var toId = request.params.toId;
	var query = new Parse.Query(Parse.User);
	query.get(fromId, {
		success : function(from) {
			console.log("First query successful.");
			var innerQuery = new Parse.Query(Parse.User);
			innerQuery.get(toId, {
				success : function(to) {
					console.log("Second query successful.");
					from.get('friends').push(to);
					to.get('friends').push(from);
					Parse.Object.saveAll([ to, from ], {
						success : function(list) {
							response.success("Yay.");

						},
						error : function(error) {
							response.error(error);

						},

					});

				},
				error : function(error) {
					response.error(error);
				}
			});

		},
		error : function(error) {
			console.log("First query error." + error);
			response.error(error);
		}
	});

});
