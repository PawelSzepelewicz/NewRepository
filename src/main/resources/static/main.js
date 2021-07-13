$(document).ready(
    function () {
        $("#userForm").submit(function (event) {
            event.preventDefault()
            removeMessages()
            userPost()
        })

        function userPost() {
            const formData = {
                userName: $('#userName').val(),
                description: $('#description').val(),
            }
            $.ajax({
                type: "POST",
                url: "http://localhost:8080/users",
                data: JSON.stringify(formData),
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function (data) {
                    resetData()
                    $('.form-message').text('User has been created successfully.')
                },
                error: function (errMsg) {
                    errMsg.responseJSON.forEach(error => {
                        let field = '#' + error.field
                        $(field).closest('.form-group').append('<div class="invalid-feedback">' + error.field + ' ' + error.message + '</div>')
                        $(field).addClass('is-invalid')
                        $(field).val('')
                    })
                }
            })
        }

        function resetData() {
            $(':input','#userForm').not(':button, :submit, :reset, :hidden').val('')
                .removeAttr('checked')
                .removeAttr('selected')
        }

        function removeMessages() {
            $('.form-message').empty()
            $('.invalid-feedback').remove()
            $('.is-invalid').removeClass('is-invalid')
        }
    })