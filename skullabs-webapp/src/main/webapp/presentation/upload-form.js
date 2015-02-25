$(function(){
	
	var UPLOAD_PROCESSING_URL = "presention/upload/status/{{id}}"
	var id = {{id}}
	var processing = {{processing}}
	var numberOfSlides = {{numberOfSlides}}

	var view = observable({

		formValues: select( "form" ).bindNames(),

		formValidation: select( "form" ).formValidation([
			"#title",{
				element: "#uploadField",
				message: "Please select a PDF file.",
				required: function(){
					return !numberOfSlides && this.formValues().showSlides()
				}
		}]),

		showFormBlankSlate: select( ".slide-blank-slate-form" ).visible( id == 0 ),

		showSlidePanel: select( ".slides-panel" ).visible( processing || numberOfSlides > 0 ),

		showUploadLoadingBar: select( ".loading-slides" ).visible( processing ),

		showFileUpload: select( "#uploadField" ).visible(),

		showSlides: select( ".slide-player" ).visible( !processing ),

		slidePlayer: select( ".slides-panel .slide-player" ).slides(),
		
		onClickShowSlides: select( "#showSlides" ).bind( "click", function( checkbox ){
			this.fixElementVisibilityRelatedToCheckbox()
		}),

		uploadSocket: websocket( UPLOAD_PROCESSING_URL )
			.on( "message", function( e ){
				if ( e.data.indexOf("done:") >= 0 ) {
					view.showUploadLoadingBar( false )
					view.showSlides( true )
					var numberOfSlides = e.data.split(":")[1]
					view.updatedNumberOfSlides( numberOfSlides )
				}
			}),

		waitForPDFProcessing: function(){
			this.uploadSocket().start()
		},

		fixElementVisibilityRelatedToCheckbox: function(){
			this.showFileUpload( this.formValues().showSlides() )
		},

		updatedNumberOfSlides: function( num ) {
			this.slidePlayer().loadSlidesFrom( id, num )
			this.formValues().numberOfSlides( num )
			numberOfSlides = num
		},

		start: function(){
			if ( processing )
				this.waitForPDFProcessing()
			if ( !processing && id > 0 )
				this.updatedNumberOfSlides( numberOfSlides )
			this.fixElementVisibilityRelatedToCheckbox()
		}
	})

	view.start()
})