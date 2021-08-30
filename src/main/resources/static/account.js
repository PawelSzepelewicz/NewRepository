$(document).ready(
    function () {
        $('#userForm').submit(function (event) {
            event.preventDefault()
            removeMessages()
            userPost()
        })
        $('#home').click(function (event) {
            window.location = '/home'
        })
        $('#confirm').click(function (event) {
            confirmRegistration()
        })

        function userPost() {
            const formData = {
                username: $('#username').val(),
                description: $('#description').val(),
                password: $('#password').val(),
                email: $('#email').val()
            }
            $.ajax({
                type: 'POST',
                url: `${host}/accounts`,
                data: JSON.stringify(formData),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: function (data) {
                    resetData()
                    $('.form-message').text('User has been created successfully.')
                    console.log(host);
                },
                error: function (errMsg) {
                    errMsg.responseJSON.forEach(error => {
                        let field = '#' + error.field
                        $(field).closest('.form-group').append(`<div class="invalid-feedback">${error.field} ${error.message}</div>`)
                        $(field).addClass('is-invalid')
                        $(field).val('')
                    })
                }
            })
        }

        function resetData() {
            $(':input', '#userForm').not(':button, :submit, :reset, :hidden').val('')
                .removeAttr('checked')
                .removeAttr('selected')
        }

        function removeMessages() {
            $('.form-message').empty()
            $('.invalid-feedback').remove()
            $('.is-invalid').removeClass('is-invalid')
        }

        function confirmRegistration() {
            const paramsString = document.location.search;
            const searchParams = new URLSearchParams(paramsString);
            $.ajax({
                type: 'GET',
                url: `${host}/accounts?${searchParams}`,
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: function (data) {
                    window.location = `${host}/login`
                }
            })
        }
    })