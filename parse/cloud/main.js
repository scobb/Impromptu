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
	
	q1.include('groups');
	q2.include('groups');
	
	// execute both queries in parallel. When they complete...
	Parse.Promise.when([q1.get(fromId), q2.get(toId)]).then(function(from, to) {
		console.log("from: " + from);
		console.log("to: " + to);
		var fromFriends = from.get('friends');
		var toFriends = to.get('friends');
		var fromGroups = from.get('groups');
		var toGroups = to.get('groups');
		
		fromGroups.forEach(function(group) {
			// find 'to' in any of 'from's groups, remove
			var groupIds = _.map(group.get('friendsInGroup'), function(friend) {
				return friend.id;
			})
			console.log('from, groupIds: ' + groupIds);
			if (groupIds.indexOf(to.id) > -1) {
				console.log('Found friend ' + to.id + ' in group ' + group.id);
				group.get('friendsInGroup').splice(groupIds.indexOf(to.id), 1);
			} else {
				console.log('This friend not in group ' + group.id);
			}
		})
		toGroups.forEach(function(group) {
			// find 'from' in any of 'to's groups, remove
			var groupIds = _.map(group.get('friendsInGroup'), function(friend) {
				return friend.id;
			})
			console.log('to, groupIds: ' + groupIds);
			if (groupIds.indexOf(from.id) > -1) {
				console.log('Found friend ' + from.id + ' in group ' + group.id);
				group.get('friendsInGroup').splice(groupIds.indexOf(from.id), 1);
			} else {
				console.log('This friend not in group ' + group.id);
			}
		})
		
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

Parse.Cloud.define("pushTest", function(request, response) {
	Parse.Cloud.useMasterKey();
	var eventId = request.params.eventId;
	var q = new Parse.Query("Event");
	q.include('owner');
	var msg, owner, title;
	q.get(eventId).then(function(event) {
		owner = event.get('owner');
		msg = event.get('description');
		title = event.get('title');
		var rel = event.relation('pushFriends');
		var q2 = rel.query();
		return q2.find();
	}).then(function(pushFriends) {
		console.log('pushFriends: ' + pushFriends);
		var q3 = new Parse.Query('Installation');
		q3.containedIn('user', pushFriends);
		Parse.Push.send({
			where: q3,
			data: {
				alert: owner.get("name") + ' just invited you to ' + title + ': ' + msg
			}
		}, 
			{ 
				success: function() {
				console.log("Yay, success.");
				response.success("yay.");
			},
				error: function(error){
					response.error(error);
			}
			
		});
		
	}), function(error) {
		response.error(error);
	}
	
})

Parse.Cloud.define("addNewEvent", function(request, response) {
	Parse.Cloud.useMasterKey();
	var Event = Parse.Object.extend("Event");
	var query = new Parse.Query(Event);
	var eventId = request.params.eventId;
	query.include('owner');
	var msg, owner, title;
	query.get(eventId).then(function(event) {
		owner = event.get('owner');
		msg = event.get('description');
		title = event.get('title');
		var relation = event.relation("streamFriends");
		var rel2 = event.relation('pushFriends');
		var q2 = relation.query();
		var q3 = rel2.query();
		var queries = [];

		// query to handle push friends
		queries.push(
			q3.find().then(function (pushFriends) {
				console.log('pushFriends: ' + pushFriends);
				var q3 = new Parse.Query('Installation');
				q3.containedIn('user', pushFriends);
				return Parse.Push.send({
					where: q3,
					data: {
						alert: owner.get("name") + ' just invited you to ' + title + ': ' + msg
					}
				});
				
			})
		)
		
		// query to handle stream friends
		queries.push(
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
				return Parse.Object.saveAll(streamFriends);
			})
		)
	
		return Parse.Promise.when(queries);
	}).then(function() {
		response.success('rocked it.');
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
		console.log("now: " + now);
		var targetTime = now - 3600 * 24;
		for (var i = 0; i < events.length; i++) {
			var event = events[i];
			console.log('eventTime: ' + event.get("eventTime").getTime());
			if (!event.get("pushed")) {
				toDelete.push(event);
			} else if (event.get("eventTime").getTime() / 1000 < targetTime) {
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
	var q = new Parse.Query(Parse.User);
	var q2 = new Parse.Query(Parse.User);
	
	Parse.Promise.when([q.get(fromId), q2.get(toId)]).then(function(from, to) {
		var q3 = new Parse.Query('Installation');
		q3.equalTo('user', from);
		var promise = Parse.Push.send({
			where: q3,
			data: {
				alert: to.get("name") + ' accepted your friend request.'
			}
		});
		console.log("Queries successful..");
		var fromIds = _.map(from.get('friends'), function(friend) {
			return friend.id;
		})
		var toIds = _.map(to.get('friends'), function(friend) {
			return friend.id;
		})
		if (fromIds.indexOf(to.id) < 0) {
			from.get('friends').push(to);
		}
		if (toIds.indexOf(from.id) < 0) {
			to.get('friends').push(from);
		}
		return Parse.Promise.when([promise, Parse.Object.saveAll([ to, from ])]);
	}, function(error) {
		console.log("Error: " + error);
		response.error(error);
	}).then(function() {
		response.success('boom');
	})

});
