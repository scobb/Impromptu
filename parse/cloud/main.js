
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.define("eventCleanup", function(request, response) {
	Parse.Cloud.useMasterKey();
	var Event = Parse.Object.extend("Event");
	var query = new Parse.Query(Event);
	var eventId = request.params.eventId;
	query.get(eventId, {
		success: function(event) {
			var innerQuery = new Parse.Query(Parse.User);
			// users whose events array contain the event in question
			innerQuery.equalTo("events", event)
			innerQuery.find({
				success: function(users) {
					for (var j = 0; j < users.length; j++) {
						user = users[j];
						var events = user.get("events");
						for (var i = events.length-1; i >= 0; i--) {
							if (event.id == eventId) {
								// remove the event from their array
								events.splice(i, 1);
							}
						}
					}
					// persist updated users
    				Parse.Object.saveAll(users, {
    					success: function(list) {
    						// destroy the event
    						event.destroy({
    							success: function() {
    	    				    	response.success("Yay.");
    								
    							},
    							error: function(error) {
    								response.error(error);
    							}
    						})
    						
    					},
	    				error: function(error) { 
	    			    	response.error(error);
	    					
	    				},
    			
    				});
					
				},
				error: function(error) {
					reseponse.error(error)
				}
			})
		},
		error: function(object, error) {
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
    	success: function(from) {
    		console.log("First query successful.");
    		var innerQuery = new Parse.Query(Parse.User);
    		innerQuery.get(toId, {
    			success: function(to) {
    				console.log("Second query successful.");
    				from.get('friends').push(to);
    				to.get('friends').push(from);
    				Parse.Object.saveAll([to, from], {
    					success: function(list) {
    				    	response.success("Yay.");
    						
    					},
	    				error: function(error) { 
	    			    	response.error(error);
	    					
	    				},
    			
    				});
    				
    			},
	        	error: function(error) {
			    	response.error(error);
	        	}
    		});

    	},
    	error: function(error) {
    		console.log("First query error." + error);
    		response.error(error);
    	}
    });
    
});

 