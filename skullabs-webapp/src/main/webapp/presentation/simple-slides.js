!function(){

	var SLIDE = "<li><img src=\"/presentation/images/%presentationIdentifier%/%slideNumber%.png\" /></li>"

	// Slide Builder API	
	helden.select.extensions.slides = function(){
		var slides = null
		var onUpdate = null
		var startSlide = 0

		this.configure = function( view, model ){
			slides = new Slides( view, model, onUpdate, startSlide )
			// return as getter
			return function(){
				return slides
			}
		}

		this.onUpdate = function( callback ){
			onUpdate = callback
			return this
		}

		this.startSlide = function( startSlide ){
			startSlide = startSlide
			return this
		}
	}

	// Slide Public API
	function Slides( view, model, onUpdate, currentSlide ) {
		var slides = this
		var slideContainer = view.find( "ul" )
		var slideNodes = view.find( "li" )

		// public
		this.goTo = function( slideNumber ){
			slideNodes.hide()
			$( slideNodes[ slideNumber ] ).show()
			currentSlide = slideNumber
			notifyUpdate()
			return this
		}

		this.next = function(){
			var nextSlide = currentSlide+1
			if ( nextSlide < slideNodes.length )
				return this.goTo( nextSlide )
		}

		this.prev = function(){
			var nextSlide = currentSlide-1
			if ( nextSlide >= 0 )
				return this.goTo( nextSlide )
		}

		this.loadSlidesFrom = function( presentationIdentifier, numberOfSlides ){
			for ( var i=0; i<numberOfSlides; i++ ) {
				var slide = SLIDE
					.replace( "%presentationIdentifier%", presentationIdentifier )
					.replace( "%slideNumber%", i )
				slideContainer.append( slide )
			}
			slideNodes = view.find( "li" )
			this.goTo(0)
		}

		// private
		function notifyUpdate(){
			if ( onUpdate )
				onUpdate.call( model, currentSlide )
		}

		// constructor
		view.find( ".control-left" ).on( "click", function(){
			console.log("prev")
			slides.prev()
		})

		view.find( ".control-right" ).on( "click", function(){
			console.log("next")
			slides.next()
		})

		slides.goTo( currentSlide )
	}

}()