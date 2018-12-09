var contextPath;
jQuery(document).ready(function($){
    contextPath = $('meta[name=context-path]').attr("content");
    contextPath =contextPath.substr(0, contextPath.length - 1);

    console.log('contextPath: ', contextPath);
	/*
	 $("#MyButton").bind("click", function() {
		  fHardCodedFunction.apply(this, [someValue]);
	 });
	 */
	//$("#cart-item-count").bind("click", updateCartItemCount);
	updateCartItemCount();
});

	function updateCartItemCount()
	{
		$.ajax ({ 
	        url: contextPath + '/cart/items/count',
	        type: "GET", 
	        dataType: "json",
	        contentType: "application/json",
	        complete: function(responseData, status, xhttp){ 
	        	$('#cart-item-count').text('(' + responseData.responseJSON.count + ')');
	        }
	    });
	}

	function addItemToCart(sku)
	{
		$.ajax ({
	        url: contextPath + '/cart/items',
	        type: "POST", 
	        dataType: "json",
	        contentType: "application/json",
	        data : '{"sku":"'+ sku +'"}"',
	        complete: function(responseData, status, xhttp){
	        	updateCartItemCount();
	        	/*
	        	$.bootstrapGrowl("Item added to cart", 
	        					{ type: 'info',
	        						offset: {
						    			from: "top",
						    			amount: 50
						    		}
	        					}
	        	);
	        	*/
	        }
	    }); 
	}

	function updateCartItemQuantity(sku, quantity)
	{
		$.ajax ({ 
	        url: contextPath + '/cart/items',
	        type: "PUT", 
	        dataType: "json",
	        contentType: "application/json",
	        data : '{ "product" :{ "sku":"'+ sku +'"},"quantity":"'+quantity+'"}',
	        complete: function(responseData, status, xhttp){ 
	        	updateCartItemCount();        	
	        	location.href = contextPath + '/cart'
	        }
	    });
	}

	function removeItemFromCart(sku)
	{
		$.ajax ({ 
	        url: contextPath + '/cart/items/'+sku,
	        type: "DELETE", 
	        dataType: "json",
	        contentType: "application/json",
	        complete: function(responseData, status, xhttp){ 
	        	updateCartItemCount();
	        	location.href = contextPath + '/cart'
	        }
	    });
	}

