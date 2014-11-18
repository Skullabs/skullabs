window.Socket = (function(){

	function Socket( endpoint ) {

		var listeners = {
			error: [],
			messages: [],
			open: []
		}

		this.on = function( eventType, callback ){
			listeners[ eventType ].push( callback )
			return this
		}
		
		function dispatch( eventType, event ) {
			var callbacks = listeners[eventType]
			for ( var i=0; i<callbacks.length; i++ )
				callbacks[i]( event )
		}
		
		function createDispatcherFor( eventType ){
			return function( event ){
				dispatch( eventType, event )
			}
		}
		
		var protocol = document.location.protocol.replace("http", "ws")
		var host = document.location.host
		var websocket = new WebSocket( protocol + "//" + host + "/" + endpoint )
		websocket.onopen = createDispatcherFor( "open" )
		websocket.onclose = createDispatcherFor( "close" )
		websocket.onmessage = createDispatcherFor( "message" )
	}
	
	return Socket
})()