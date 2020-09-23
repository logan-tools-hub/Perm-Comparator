"use strict";
var CollabrationJs = {
	init: function() {
		var _this = this;

		_this.globalPermissions = {};
		_this.registerEvents();
		_this.generateHandlebars();
	},
	registerEvents : function() {
		var _this = this;
		
		$("body").css("max-height", window.innerHeight+"px");
		$(".cls_side_linksWrap").height(window.innerHeight - ($(".cls_collabrationHeaderWrap").height() + $(".cls_collabrationFooterWrap").height()));
		$(".cls_tabContent").height($(".cls_side_linksWrap").height() - $(".cls_sideToggle").height() * $(".cls_sideToggle").length);
		
		//TODO: 7px needs to be calculated.
		$(".cls_permissionItemsWrap").height(window.innerHeight - ($(".cls_collabrationHeaderWrap").height() + $(".cls_collabrationFooterWrap").height() +($(".cls_permHeaderWrap").height() * $(".cls_permHeaderWrap").length) + ($(".cls_permissionWrap").height() * 3) + 7));
		$(".form-check-input").off("change").on("change",function() {
			if($(this).is(":checked")) {
				var config = {
					userId : $(".cls_userName").attr("userid"),
					Id : $(this).attr("id"),
					type : $(this).attr("perm_type"),
					name :  $(this).next().text(),
				};
				
				if(typeof _this.globalPermissions[config.Id] != "undefined") {
					var response = _this.globalPermissions[config.Id];
					var objectPermission = response.objectPermission || "";
					var userPermission = response.userPermission || "";
					var setupEntityPermission = response.setupEntityPermission || "";
					_this.renderObjPermsColumn(objectPermission, config);
					_this.renderUserPermsColumn(userPermission, config);
				} else {				
					_this.retrievePermissionSet(config);
				}
			} else {
				var name = $(this).next().text();
				$(".cls_fixedHeader .cls_columnHeader[name='"+name+"']").remove();
				$(".cls_colmunItem[name='"+name+"']").remove();
				
				_this.renderCommonUserPerms();
				_this.renderCommonObjPerms();
			}
		});
		
		$(".cls_userSearchInput,.cls_permissionSearchInput,.cls_profileSearchInput").off("keyup").on("keyup", function(){
			var serachText = $(this).val();
			
			if(serachText) {
				var parent = $(this).closest(".cls_tabMenus");
				parent = $(parent).attr("id");
				var clsName = ($(this).hasClass("cls_userSearchInput") ? ".cls_userCheckboxLabel" : ($(this).hasClass("cls_permissionSearchInput") ? ".cls_permissionCheckboxLabel" : ".cls_profileCheckboxLabel"));
				$("#"+parent + " .cls_menuItem").addClass("d-none");
				for( var i=0; i<$(clsName).length; i++) {
					var checkboxval = $($(clsName)[i]).text();
					checkboxval = (checkboxval != "" && typeof checkboxval != "undefined") ? checkboxval.toLowerCase() : "";
				    if(checkboxval.indexOf(serachText.toLowerCase()) != -1)  {
				    	$($(clsName)[i]).closest(".cls_menuItem").removeClass("d-none");
				 	}
				}
			} else {
				var menu = $(this).closest(".cls_tabMenus");
				$(menu).find(".cls_menuItem").removeClass("d-none");
			}
		});
	},
	renderCommonObjPerms: function() {
		var _this = this;
		
		/*  Rendering Common User Perms */
			var arrayOfObjPermObj = [];
			for( var i=0; i<$("#id_commonObjPerm .cls_columnHeader").length; i++) {
				var permId = $($("#id_commonObjPerm .cls_columnHeader")[i]).attr("permid");
				arrayOfObjPermObj.push(_this.globalPermissions[permId].objectPermission); 
			}
			
		var commonObj = {};
		var records = [];
		var firstRecords = arrayOfObjPermObj[0].records;
		if(arrayOfObjPermObj.length > 1) {		
		    for( var i=0; i<firstRecords.length; i++) {
		        var currentObjType = firstRecords[i].SobjectType;
		        commonObj[currentObjType] = [];
		        commonObj[currentObjType].push(firstRecords[i]);
		        var isCommon = true;
		        for( var j=1; j<arrayOfObjPermObj.length; j++) {
		           var innerRecords =  arrayOfObjPermObj[j].records;
		           var isFound = false;
		           if(innerRecords.length) { //TODO: Test
			            for( var k=0; k<innerRecords.length; k++) {
			                if(innerRecords[k].SobjectType == currentObjType ) {
			                   commonObj[currentObjType].push(innerRecords[k]);
			                   isFound = true; 
			                }
			            }
			            if(!isFound) {
			            	delete commonObj[currentObjType];
			            	break;
			            }		           
		           }
		            
		        }
		       
		    }
		}

		var records = [];
		Object.keys(commonObj).forEach(function(key,index) {
		    var objName = key;
		    var currentArr = commonObj[key], firstObj = currentArr[0];
	        
	        var isPermissionsCreate = true, isPermissionsDelete = true, isPermissionsEdit = true, isPermissionsModifyAllRecords = true, isPermissionsRead = true, isPermissionsViewAllRecords = true;

	        for( var j=1; j<currentArr.length; j++) {
	            if(firstObj.PermissionsCreate != currentArr[j].PermissionsCreate) {
	                isPermissionsCreate = false;
	            }
	            if(firstObj.PermissionsDelete != currentArr[j].PermissionsDelete) {
	                isPermissionsDelete = false;
	            }
	            if(firstObj.PermissionsEdit != currentArr[j].PermissionsEdit) {
	                isPermissionsEdit = false;
	            }
	            if(firstObj.PermissionsModifyAllRecords != currentArr[j].PermissionsModifyAllRecords) {
	                isPermissionsModifyAllRecords = false;
	            }
	            if(firstObj.PermissionsRead != currentArr[j].PermissionsRead) {
	                isPermissionsRead = false;
	            }
	            if(firstObj.PermissionsViewAllRecords != currentArr[j].PermissionsViewAllRecords) {
	                isPermissionsViewAllRecords = false;
	            }
	
	        }
	        var comObj = {
	            "SobjectType" : firstObj.SobjectType
	        };
	        
	        if(isPermissionsCreate) {
	           comObj.PermissionsCreate = firstObj.PermissionsCreate;
	        }
	        if(isPermissionsDelete) {
	            comObj.PermissionsDelete = firstObj.PermissionsDelete;
	        }
	        if(isPermissionsEdit) {
	            comObj.PermissionsEdit = firstObj.PermissionsEdit;
	        }
	        if(isPermissionsModifyAllRecords) {
	            comObj.PermissionsModifyAllRecords = firstObj.PermissionsModifyAllRecords;
	        }
	        if(isPermissionsRead) {
	            comObj.PermissionsRead = firstObj.PermissionsRead;
	        }
	        if(isPermissionsViewAllRecords) {
	            comObj.PermissionsViewAllRecords = firstObj.PermissionsViewAllRecords;
	        }
		    records.push(comObj);
		});
		
		
		$("#id_commonObjPerm .cls_colmunItem").remove();
		for( var i=0; i<arrayOfObjPermObj.length; i++) {
			var res = {};
			var permId = $($("#id_commonObjPerm .cls_columnHeader")[i]).attr("permid");			
	 		var template = Handlebars.compile(_this.tempTxt);
	 		res.header = _this.globalPermissions[permId].objectPermission.header;
	 		res.records = (_this.globalPermissions[permId].objectPermission.records.length ? records : []);
			var finalContent = template(res);
			$("#id_commonObjPerm .cls_permissionItemsWrap").append(finalContent);
		}
		
		//var uniqueObj = Object.create(arrayOfObjPermObj);
		 
		var uniqueObjRes = _this.consolidateUniqueObj();
		console.log("=====================================");
		console.log(uniqueObjRes);
		console.log("=====================================");
		
		
		$("#id_uniqueObjPerm .cls_colmunItem").remove();
		for( var i=0; i<uniqueObjRes.length; i++) {
			var res = {};
			var permId = $($("#id_uniqueObjPerm .cls_columnHeader")[i]).attr("permid");			
	 		var template = Handlebars.compile(_this.tempTxt);
	 		res.header = _this.globalPermissions[permId].objectPermission.header;
	 		res.records = (uniqueObjRes[i].records.length ? uniqueObjRes[i].records : []);
			var finalContent = template(res);
			$("#id_uniquePermWrap .cls_objPermissionList .cls_permissionItemsWrap").append(finalContent);
		}
	},
	
	consolidateUniqueObj : function() {
		var _this = this;
		
		/*  Rendering Common User Perms */
			var uniqueObj = [];
			for( var i=0; i<$("#id_commonObjPerm .cls_columnHeader").length; i++) {
				var permId = $($("#id_commonObjPerm .cls_columnHeader")[i]).attr("permid");
				uniqueObj.push(_this.globalPermissions[permId].objectPermission); 
			}
			
		var currentRecord = _this.findMaxRecordPerm(uniqueObj);
		var toRet = [];
		var conUniqueObj = {};
		for( var i=0; i<currentRecord.records.length; i++) {
		   var currentObj = currentRecord.records[i];
		   conUniqueObj[currentObj.SobjectType] = {};
		   conUniqueObj[currentObj.SobjectType][currentRecord.header] = currentObj;
		
		    for( var j=0; j<uniqueObj.length; j++) {
		        if(uniqueObj[j].header != currentRecord.header) {
		            for( var k=0; k<uniqueObj[j].records.length; k++) {
		                if(uniqueObj[j].records[k].SobjectType == currentObj.SobjectType) {
		                    conUniqueObj[currentObj.SobjectType][uniqueObj[j].header] = uniqueObj[j].records[k];
		                }
		            }
		        }
		    }
		}
		
		console.log("conUniqueObj:");
		console.log(conUniqueObj);
		
		
		for( var i=0; i<uniqueObj.length; i++) {
		    var header = uniqueObj[i].header;
		    //var responsetoRet = uniqueObj[i];
		    var responsetoRet = Object.create(uniqueObj[i]);
		    var toReturnRec = [];
		    Object.keys(conUniqueObj).forEach(function(key,index) {
		        var curr = conUniqueObj[key][header];
		    
		        var currObj = conUniqueObj[key];
		        currObj.current = header;
		        var isObjUnique = true;
		        var isPermissionsCreate = true, isPermissionsDelete = true, isPermissionsEdit = true, isPermissionsModifyAllRecords = true, isPermissionsRead = true, isPermissionsViewAllRecords = true;
		        Object.keys(currObj).forEach(function(innerKey,innerIndex) {
		        	curr =  currObj[currObj.current];
		            if(curr && innerKey != header && innerKey != "current") {
		              isObjUnique = false;
		              if(curr.PermissionsCreate == currObj[innerKey].PermissionsCreate) {
			                isPermissionsCreate = false;
			            }
			            if(curr.PermissionsDelete == currObj[innerKey].PermissionsDelete) {
			                isPermissionsDelete = false;
			            }
			            if(curr.PermissionsEdit == currObj[innerKey].PermissionsEdit) {
			                isPermissionsEdit = false;
			            }
			            if(curr.PermissionsModifyAllRecords == currObj[innerKey].PermissionsModifyAllRecords) {
			                isPermissionsModifyAllRecords = false;
			            }
			            if(curr.PermissionsRead == currObj[innerKey].PermissionsRead) {
			                isPermissionsRead = false;
			            }
			            if(curr.PermissionsViewAllRecords == currObj[innerKey].PermissionsViewAllRecords) {
			                isPermissionsViewAllRecords = false;
			            }  
		            }
		        });
		        
		        var obj = {};
		        if(isObjUnique && curr) {
		        	toReturnRec.push(curr); 
		        } else {
		        	if(isPermissionsCreate && curr && curr.PermissionsCreate) {
			           obj.PermissionsCreate = curr.PermissionsCreate;
			        }
			        if(isPermissionsDelete && curr &&  curr.PermissionsDelete) {
			            obj.PermissionsDelete = curr.PermissionsDelete;
			        }
			        if(isPermissionsEdit && curr && curr.PermissionsEdit) {
			            obj.PermissionsEdit = curr.PermissionsEdit;
			        }
			        if(isPermissionsModifyAllRecords && curr &&  curr.PermissionsModifyAllRecords) {
			            obj.PermissionsModifyAllRecords = curr.PermissionsModifyAllRecords;
			        }
			        if(isPermissionsRead && curr && curr && curr.PermissionsRead) {
			            obj.PermissionsRead = curr.PermissionsRead;
			        }
			        if(isPermissionsViewAllRecords && curr && curr.PermissionsViewAllRecords) {
			            obj.PermissionsViewAllRecords = curr.PermissionsViewAllRecords;
			        }
			        
			        if(Object.keys(obj).length > 0) {
			            obj.SobjectType = curr.SobjectType;
			            toReturnRec.push(obj);
					}
		        }
		        
		       
			        
		    });
		    console.log(toReturnRec);
		    responsetoRet.records = toReturnRec;
		    toRet.push(responsetoRet);
		}
			
		return toRet;
	},
	findMaxRecordPerm : function(uniqueObj) {
	    var max = uniqueObj[0].records.length;
	    var maxPerm = uniqueObj[0];
	    for( var i=0; i<uniqueObj.length; i++) {
	        if(max < uniqueObj[i].records.length) {
	            max = uniqueObj[i].records.length
	            maxPerm = uniqueObj[i];
	        }
	    }
	    
	    return maxPerm;
	},
	retrievePermissionSet: function(prop) {
		var _this = this;
		
			var successCbk = function(response) {
				
				$(".cls_mask").addClass("d-none");
				$("body").removeClass("position-fixed");
				console.dir("Inside successCbk");
				var objectPermission = (response.objectPermission ? JSON.parse(response.objectPermission) : "");
				var userPermission = (response.userPermission ? JSON.parse(response.userPermission) : "");
				var setupEntityPermission = (response.setupEntityPermission ? JSON.parse(response.setupEntityPermission) : "");
				setupEntityPermission.header = prop.name;
				
				/* Consolidating the response start */
				objectPermission.records = _this.consolidateObjectPerms(objectPermission.records);
				userPermission.records = _this.consolidateUserPerm(userPermission, prop.type);
				
				
				
				/* Consolidating the response end */
				response.objectPermission = objectPermission;
				response.userPermission = userPermission;	
				response.setupEntityPermission = setupEntityPermission;
				_this.globalPermissions[prop.Id] = response;
				
				_this.renderObjPermsColumn(objectPermission, prop);
				_this.renderUserPermsColumn(userPermission, prop);
				_this.renderUserSetupColumn(setupEntityPermission, prop);
				
			};
		
			var failure = function(response) {
				$(".cls_mask").addClass("d-none");
				console.dir("Inside failureCbk");
				console.debug(response);
			};
			
			var config = {
					url: location.origin + "/query/"+prop.userId + "/" + prop.Id + "/" + prop.type,
					type: "GET",
					headers: {
					    "Content-Type" : "application/json"
					  },
					success: successCbk,
					error: failure
			};
		
			$("body").addClass("position-fixed");
			$(".cls_mask").removeClass("d-none");	
			$.ajax(config);
	},
	renderUserSetupColumn : function(response, config) {
		var _this = this;
		
		if(Handlebars) {
			var template = Handlebars.compile(_this.appMenu);
			response.header = config.name;
			response.entityType = "Apps";
			var finalContent = template(response);
			$("#id_generalSetupPerm .cls_fixedHeader").append('<div class="cls_columnOneHeader cls_columnHeader" permId="'+ config.Id +'" name="'+ config.name +'">'+ config.name +'</div>');
			$("#id_generalPermWrap #id_generalSetupPerm .cls_permissionItemsWrap").append(finalContent);
		}
		
	},
	renderObjPermsColumn : function (response, config) {
		var _this = this;
		//response = JSON.parse(response);
		if(Handlebars) {
			var template = Handlebars.compile(_this.tempTxt);
			/*if(type == "user") {
				response.records = _this.consolidateObjectPerms(response.records);			
			}*/
			response.header = config.name;
			var finalContent = template(response);
			$(".cls_objPermissionList .cls_fixedHeader").append('<div class="cls_columnOneHeader cls_columnHeader" permId="'+ config.Id +'" name="'+ config.name +'">'+ config.name +'</div>');
			$("#id_generalPermWrap .cls_objPermissionList .cls_permissionItemsWrap").append(finalContent);
			console.dir(response);
		}
		
		
		
			_this.renderCommonObjPerms();
		
	},
	renderUserPermsColumn : function (response, config) {
		var _this = this;
		if(Handlebars) {
			
			var template = Handlebars.compile(_this.userPermTxt);
			response.header = config.name;			
			var finalContent = template(response);
			$(".cls_userPermissionList .cls_fixedHeader").append('<div class="cls_columnOneHeader cls_columnHeader" permId="'+ config.Id +'" name="'+ config.name +'">'+ config.name +'</div>');
			$("#id_generalPermWrap .cls_userPermissionList .cls_permissionItemsWrap").append(finalContent);
			
			
			_this.renderCommonUserPerms();
		}
		
	},
	renderCommonUserPerms : function(arrayOfUserPermObj) {
		var _this = this;
		
		/*  Rendering Common User Perms */
		var arrayOfUserPermObj = [];
		for( var i=0; i<$("#id_commonUserPerm .cls_columnHeader").length; i++) {
			var permId = $($("#id_commonUserPerm .cls_columnHeader")[i]).attr("permid");
			arrayOfUserPermObj.push(_this.globalPermissions[permId].userPermission); 
		}
		
		var response = {};
		
		var firstRecords = arrayOfUserPermObj[0].records;
		var records = [];
		if(arrayOfUserPermObj.length > 1) {		
			for( var i=0; i<firstRecords.length; i++) {
			    var item = firstRecords[i];
			    var isCommon = true;
			    for( var j=1; j<arrayOfUserPermObj.length; j++) {
			        if(arrayOfUserPermObj[j].records.length && $.inArray(item,arrayOfUserPermObj[j].records) == -1) {
			             isCommon = false;
			             break;
			        }
			        
			    }
			    if(isCommon) {
					records.push(item);
				}
			}
		} else {
			records = firstRecords;
		}
		response.records = records;
		
		$("#id_commonUserPerm .cls_colmunItem").remove();
		for( var i=0; i<arrayOfUserPermObj.length; i++) {
			var permId = $($("#id_commonUserPerm .cls_columnHeader")[i]).attr("permid");			
				var res = {
					header: _this.globalPermissions[permId].objectPermission.header,
					records : (_this.globalPermissions[permId].userPermission.records.length ? response.records : [])
				};
		 		var template = Handlebars.compile(_this.userPermTxt);
		 		
				var finalContent = template(res);
				$("#id_commonPermWrap .cls_userPermissionList .cls_permissionItemsWrap").append(finalContent); //TODO:checkk
		}
		_this.renderUniqueuserPerm(arrayOfUserPermObj);
	},
	renderUniqueuserPerm: function(arrayOfUserPermObj) {
		var _this = this;
		
		var uniqueParams = {};
		for( var i=0; i<arrayOfUserPermObj.length; i++) {
		   var curObj = arrayOfUserPermObj[i];
		   uniqueParams[curObj.header] = {};
		   uniqueParams[curObj.header].records = [];
		   for( var j=0; j<curObj.records.length; j++) {
		       var permName = curObj.records[j]; 
		       var isUnique = true;
		       for( var k=0; k<arrayOfUserPermObj.length; k++) {
		        if(curObj.header != arrayOfUserPermObj[k].header) {
			        if($.inArray(permName,arrayOfUserPermObj[k].records) != -1) {
			            isUnique = false;
			            break;
			        }		            
		        }
		      }
		      if(isUnique) {
		          uniqueParams[curObj.header].records.push(permName);
		          //records.push(permName);  
		      }
		   }
		
		}
		
		console.dir(uniqueParams);
		$("#id_uniqueUserPerm .cls_colmunItem").remove();
		for( var i=0; i<$("#id_uniqueUserPerm .cls_columnHeader").length; i++) {
			var permName = $($("#id_uniqueUserPerm .cls_columnHeader")[i]).text();	
			var response = uniqueParams[permName];
			var template = Handlebars.compile(_this.userPermTxt);
			response.header = permName;
			var finalContent = template(response);
			$("#id_uniquePermWrap .cls_userPermissionList .cls_permissionItemsWrap").append(finalContent); //TODO: //Check
		}
		
	},
	consolidateUserPerm: function(userPerm, type) {
		var records = userPerm.records;
		var userPermision = records[0]
		
		var toRet = [];
		if(type == "user" && records.length > 1) {
			for( var i=1; i<records.length; i++) {
			    Object.keys(records[i]).forEach(function(key,index) {
			        if(records[i][key] == true) {
			           userPermision[key] = true; 
			        }
			    });
			}		
		}
		
		if(typeof userPermision != "undefined") {		
			Object.keys(userPermision).forEach(function(key,index) {
				if(key != "Id" && key.toLowerCase() != "attributes" && userPermision[key]) {
					var text = key.replace("Permissions", "");
					var result = text.replace( /([A-Z][a-z])/g, " $1" );
					var result = result.charAt(0).toUpperCase() + result.slice(1);
					toRet.push(result.trim());				
				}
		    });	
		}
		toRet = toRet.sort();
		return toRet;
	},
	consolidateObjectPerms : function(objPerm) {
		var consolidatedObj = [];
		var temp = [];
		
		for( var i=0; i<objPerm.length; i++) {
		    var curObj = objPerm[i];
		    
		    for( var j=0;j<objPerm.length; j++) {
		        if(curObj.SobjectType == objPerm[j].SobjectType && $.inArray(curObj.SobjectType, temp) == -1) {
		            if(objPerm[j].PermissionsCreate) {
						curObj.PermissionsCreate = true;
		            }
					if(objPerm[j].PermissionsDelete) {
						curObj.PermissionsDelete = true;
		            }
					if(objPerm[j].PermissionsEdit) {
						curObj.PermissionsEdit = true;
		            }
					if(objPerm[j].PermissionsModifyAllRecords) {
						curObj.PermissionsModifyAllRecords = true;
		            }
					if(objPerm[j].PermissionsRead) {
						curObj.PermissionsRead = true;
		            }
					if(objPerm[j].PermissionsViewAllRecords) {
						curObj.PermissionsViewAllRecords = true;
		            }
		            consolidatedObj.push(curObj);
		    		temp.push(curObj.SobjectType);
		        }
				
		    }
		}
		return consolidatedObj;
	},
	generateHandlebars : function(response) {
		var _this = this;
		
		_this.tempTxt = '<div class="cls_colmunItem" name="{{header}}">';
		_this.tempTxt +='{{#if records}}';
		_this.tempTxt += '{{#each records}}';
    	_this.tempTxt += '<div class="cls_objname">';
        	_this.tempTxt += '<div class="cls_ObjNameTxt">{{SobjectType}}</div>';
        	_this.tempTxt += '<div class="cls_permissionItem">';
        		_this.tempTxt +='{{#if PermissionsCreate}}';
	            _this.tempTxt += '<div class="cls_permItem"><div class="cls_checkmark"></div><div class="cls_access PermissionsCreate">Create</div></div>';
	            _this.tempTxt += '{{/if}}';
	            _this.tempTxt +='{{#if PermissionsRead}}';
	            _this.tempTxt += '<div class="cls_permItem"><div class="cls_checkmark"></div><div class="cls_access PermissionsRead">Read</div></div>';
	            _this.tempTxt += '{{/if}}';
	            _this.tempTxt +='{{#if PermissionsEdit}}';
	            _this.tempTxt += '<div class="cls_permItem"><div class="cls_checkmark"></div><div class="cls_access PermissionsEdit">Edit</div></div>';
	            _this.tempTxt += '{{/if}}';
	            _this.tempTxt +='{{#if PermissionsDelete}}';
	            _this.tempTxt += '<div class="cls_permItem"><div class="cls_checkmark"></div><div class="cls_access PermissionsDelete">Delete</div></div>';
	            _this.tempTxt += '{{/if}}';
	            _this.tempTxt +='{{#if PermissionsViewAllRecords}}';
	            _this.tempTxt += '<div class="cls_permItem"><div class="cls_checkmark"></div><div class="cls_access PermissionsViewAllRecords">ViewAll</div></div>';
	            _this.tempTxt += '{{/if}}';
	            _this.tempTxt +='{{#if PermissionsModifyAllRecords}}';
	            _this.tempTxt += '<div class="cls_permItem"><div class="cls_checkmark"></div><div class="cls_access PermissionsModifyAllRecords">ModifyAll</div></div>';
	            _this.tempTxt += '{{/if}}';
				_this.tempTxt += '</div>';
	            _this.tempTxt += '</div>';
			_this.tempTxt += '{{/each}}';
			_this.tempTxt += '{{/if}}';
			_this.tempTxt += '</div>';
		
			
		_this.userPermTxt = '<div class="cls_colmunItem" name="{{header}}">';
		_this.userPermTxt +='{{#if records}}';
		_this.userPermTxt += '<table class="cls_userPermissionTable">';
		_this.userPermTxt += '{{#each records}}';
		_this.userPermTxt += '<tr>';
    	_this.userPermTxt += '<div class="cls_objname">';
        	_this.userPermTxt += '<div class="cls_permissionItem">';
        		_this.userPermTxt += '<td><div class="cls_permItem"><div class="cls_access">{{this}}</div></div></td>';
        	_this.userPermTxt += '</div>';
		_this.userPermTxt += '</div>';
		_this.userPermTxt += '</tr>';
		_this.userPermTxt += '{{/each}}';
		_this.userPermTxt += '</table>';
		_this.userPermTxt += '{{/if}}';
		_this.userPermTxt += '</div>';
		
		
		_this.appMenu = '<div class="cls_colmunItem" name="{{header}}">';
			_this.appMenu += '<div class="cls_objname">';
        		_this.appMenu += '<div class="cls_ObjNameTxt">{{entityType}}</div>';
        		_this.appMenu += '<div class="cls_permissionItem">';
        			_this.appMenu += '{{#each appMenuItem}}';
						_this.appMenu += '<div class="cls_permItem"><div class="cls_checkmark"></div><div class="cls_access" id="id_appMenu_{{Id}}">{{Label}}</div></div>';
					_this.appMenu += '{{/each}}';
				_this.appMenu += '</div>';
			_this.appMenu += '</div>';
		_this.appMenu += '</div>';
	}
};

$(document).ready(function(){
	//initiate the javascript events
	CollabrationJs.init();
	
	if(Handlebars) {
		
		Handlebars.registerHelper('replace', function(text, options) {
			
			text = text.replace("Permissions", "");
			var result = text.replace( /([A-Z])/g, " $1" );
			var result = result.charAt(0).toUpperCase() + result.slice(1);
			
			return result;
		});
		
		
		Handlebars.registerHelper('decode', function( text, options) {
			return unescape(unescape(text));
		});
		
		
		Handlebars.registerHelper('unescape', function(content,searchTxt, options) {
			
			var unescapedConent = unescape(unescape(content));
			console.log("searchTxt: "+ searchTxt);
			unescapedConent = unescapedConent.replace(/<\/?[^>]+(>|$)/g, "");
			 return Handlebars.helpers.replace(unescapedConent, searchTxt);
		});
	}
	
});