(function ($, $document) {
    "use strict";

    var DATATYPE = "./datasourceType";

    function adjustLayoutHeight(){
        //with only two selects, the second select drop down is not visible when expanded, so adjust the layout height
        //fixedcolumns i guess doesn't support css property height, so fallback to jquery
        //http://docs.adobe.com/docs/en/aem/6-0/develop/ref/granite-ui/api/jcr_root/libs/granite/ui/components/foundation/layouts/fixedcolumns/index.html
        $(".coral-FixedColumn-column").css("height", "20rem");
    }

    // when dialog gets injected
    $document.on("foundation-contentloaded", function(e) {
        //debugger;
    });

    $document.on("dialog-ready", function() {
        adjustLayoutHeight();
        var textarea = $('#date_textarea').parent('.coral-Form-fieldwrapper');

        //http://docs.adobe.com/docs/en/aem/6-0/develop/ref/granite-ui/api/jcr_root/libs/granite/ui/components/foundation/form/select/index.html
        var sourceType = new CUI.Select({
            element: $("[name='" + DATATYPE +"']").closest(".coral-Select")
        });

        //workaround to remove the options getting added twice, using CUI.Select()
        sourceType._selectList.children().not("[role='option']").remove();

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
        });
    });
})($, $(document));