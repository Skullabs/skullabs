(function(){

	function Socket( endpoint ) {

		var self = this
		var listeners = {
			close: [],
			error: [],
			message: [],
			open: []
		}

		this.configure = function(){
			return function(){
				return self
			}
		}

		this.on = function( eventType, callback ){
			listeners[ eventType ].push( callback )
			return this
		}

		function dispatch( eventType, event ) {
			console.log( "Socket: dispatching " + eventType )
			var callbacks = listeners[eventType]
			for ( var i=0; i<callbacks.length; i++ )
				callbacks[i].call( self, event )
		}

		function createDispatcherFor( eventType ){
			return function( event ){
				dispatch( eventType, event )
			}
		}
		
		this.start = function(){
			var protocol = document.location.protocol.replace("http", "ws")
			var host = document.location.host
			var websocket = new WebSocket( protocol + "//" + host + "/" + endpoint )
			websocket.onopen = createDispatcherFor( "open" )
			websocket.onclose = createDispatcherFor( "close" )
			websocket.onmessage = createDispatcherFor( "message" )
			return this
		}

	}

	window.websocket = function( endpoint ){
		return new Socket( endpoint )
	}
})()