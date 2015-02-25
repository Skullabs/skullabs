!function(){

	helden.select.extensions.formValidation = function( model ){
		var self = this
		var validationCallback = onValidate

		model = (model instanceof Array) ? model : [ model ]
		for ( var i=0; i<model.length; i++ )
			if ( (typeof model[i]) == "string" )
				model[i] = { element: model[i] }

		this.validationCallback = function( callback ){
			validationCallback = callback
			return self
		}

		this.configure = function( view, targetModel ){
			view.submit(function(){
				return runFormValidation( view, targetModel )
			})
			return function(){}
		}

		function runFormValidation( view, targetModel ) {
			for ( var i=0; i<model.length; i++ ){
				if ( !runValidation( view, model[i], targetModel ) )
					return false
			}
			return true
		}

		function runValidation( view, validation, targetModel ) {
			var element = view.find( validation.element )
			var req = validation.required
			if ( req == undefined )
				req = true
			if ( (typeof req)=="function" )
				req = req.call( targetModel, element, view, targetModel )
			if ( req && !element.val() ) {
				makeInvalid( view, validation, targetModel, element )
				return false
			}
			return true
		}

		function makeInvalid( view, validation, targetModel, element ) {
			function reValidate(){
				element.off( "blur", reValidate )
				element.removeClass( "invalid-field" )
				onValidate( element, true )
				runValidation( view, validation, targetModel )
			}

			element.on( "blur", reValidate )
			element.addClass( "invalid-field" )
			onValidate( element, false, validation.message )
		}

		function onValidate( element, isValid, message ){
			message = message || "Required field"
			if ( !isValid ) {
				var span = $("<span class='invalid-field-message'>" + message + "</span>")
				element.after( span )
			} else {
				element.siblings( '.invalid-field-message' ).remove()
			}
		}
	}

}()
