$(function(){
	
	var UPLOAD_PROCESSING_URL = "presention/upload/status/{{id}}"
	var id = {{id}}
	var processing = {{processing}}

	var view = observable({

		showDetailedView: observable( ".detailed-view" ).visible( id != 0 ),

		showUploadForm: observable( ".upload-form" ).visible( id == 0 ),

		showUploadLoadingBar: observable( ".detailed-view .loading-bar" ).visible( processing ),

		showSlides: observable( ".detailed-view .slides" ).visible( !processing ),

		waitForPDFProcessing: function(){
			var websocket = new Socket( UPLOAD_PROCESSING_URL )
			websocket
				.on( "message", function( e ){
					if ( e.data == "done" ) {
						view.showUploadLoadingBar( false )
						view.showSlides( true )
					}
				})
				.on( "close", function( e ) {
					console.log( e )
					console.log( e.data )
				} )
		},

		start: function(){
			if ( processing )
				this.waitForPDFProcessing()
		}
	})

	view.start()
})