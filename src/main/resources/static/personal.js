$(document).ready(
    function () {
        let editable = true
        $('.edit-data').click(function (event) {
            if (editable === true) {
                $('.save-data').attr('hidden', false)
                $('.form-control-plaintext').attr('readonly', false).toggleClass('alert-secondary', true)
                $('.edit-data').text('CLOSE')
                editable = false
            } else {
                $('.save-data').attr('hidden', true)
                $('.edit-data').text('EDIT')
                $('.form-control-plaintext').attr('readonly', true).toggleClass('alert-secondary', false)
                editable = true
            }
        })
    })
