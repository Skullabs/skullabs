$(function(){
	
	var id = {{id}}
	var view = {

		/**
		 * Show detailed view
		 */
		showDetailedView: observable( ".detailed-view" ).visible( id != 0 ),

		/**
		 * Show upload form
		 */
		showUploadForm: observable( ".upload-form" ).visible( id == 0 )

	}

	observable(view)

	var websocket = new Socket( "/presentation/stream/upload/{{id}}" )
	websocket.on( "message", function( e ){
		if ( e.data == "done" )
			;
	})
})