/*
 *  Copyright (c) 2015 faizod GmbH & Co. KG
 *  Großenhainer Straße 101, D-01127 Dresden, Germany
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Clientside JS for the CQ Dialog.
 */
(function ($, $document) {
    "use strict";

    // var DATATYPE = "./datasourceType";

    $document.on("dialog-ready", function() {
        /*var textarea = $('#date_textarea').parent('.coral-Form-fieldwrapper');

        //http://docs.adobe.com/docs/en/aem/6-0/develop/ref/granite-ui/api/jcr_root/libs/granite/ui/components/foundation/form/select/index.html
        var sourceType = new CUI.Select({
            element: $("[name='" + DATATYPE +"']").closest(".coral-Select")
        });

        //workaround to remove the options getting added twice, using CUI.Select()
        //sourceType._selectList.children().not("[role='option']").remove();

        // TODO: refactor!
        if (sourceType._nativeSelect[0].selectedOptions[0].value === 'excel') {
            textarea.hide();
        }

        sourceType._selectList.on('selected.select', function(event){
            if (event.selectedValue === 'excel') {
                textarea.hide();
            } else {
                textarea.show();
            }
        });*/
    });
})($, $(document));