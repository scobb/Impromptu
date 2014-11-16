
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.define("simpleQuery", function(request, response) {
	Parse.Cloud.useMasterKey();
	var Group = Parse.Object.extend("Group");
	var query = new Parse.Query(Group);
	query.get("i8UbLj0cuo", {
		success: function(group) {
			console.log("Query successful.");
			response.success("Yay");
		},
		error: function(object, error) {
			console.log("Query unsuccessful.");
			response.error("Yay");
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

 