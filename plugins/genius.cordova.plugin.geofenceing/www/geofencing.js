var exec = require('cordova/exec');

module.exports = {

    vibrate: function(param) {
        
		//vibrate
        if ((typeof param == 'number') && param != 0)
            exec(null, null, "Geofence", "vibrate", [param]);

        //vibrate with array ( i.e. vibrate([3000]) )
        else if ((typeof param == 'object') && param.length == 1)
        {
            //cancel if vibrate([0])
            if (param[0] == 0)
                exec(null, null, "Geofence", "cancelVibration", []);

            //else vibrate
            else
                exec(null, null, "Geofence", "vibrate", [param[0]]);
        }

        //vibrate with a pattern
        else if ((typeof param == 'object') && param.length > 1)
        {
            var repeat = -1; //no repeat
            exec(null, null, "Geofence", "vibrateWithPattern", [param, repeat]);
        }

        //cancel vibration (param = 0 or [])
        else
            exec(null, null, "Geofence", "cancelVibration", []);
		
    },

    vibrateWithPattern: function(pattern, repeat) {
        
		repeat = (typeof repeat !== "undefined") ? repeat : -1;
        pattern.unshift(0); //add a 0 at beginning for backwards compatibility from w3c spec
        exec(null, null, "Geofence", "vibrateWithPattern", [pattern, repeat]);
    	
	},

    cancelVibration: function() {
        exec(null, null, "Geofence", "cancelVibration", []);
    }
};