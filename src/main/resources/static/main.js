$(document).ready(
    function () {
        getCurrentUser()
        getUsersByRating()
        getNextUsers()
        $('.card-dtn').click(function (event) {
            let winnerId = $(this).closest('.card').data('userId')
            selectUser(winnerId)
            getNextUsers()
        })
        $('#login-logout').click(function (event) {
            let isAuth = $(this).closest('.login').data('isAuth')
            if (isAuth !== 'true') {
                window.location = '/login'
            } else {
                window.location = '/logout'
            }
        })
        $('#creation').click(function (event) {
            window.location = '/registration'
        })
        $('.edit-data').click(function (event) {
            removeMessages()
            toggleButtons()
        })
        $('.save-data').click(function (event) {
            removeMessages()
            changeUsersData()
        })
        $('.password-cng').click(function (event) {
            removeMessages()
            changePassword()
        })

        let editable = true

        let isAdmin = false

        function toggleButtons() {
            if (editable === true) {
                $('.save-data').attr('hidden', false)
                $('.form-control-plaintext').attr('readonly', false).toggleClass('alert-secondary', true)
                $('.edit-data').text('CLOSE')
                editable = false
            } else {
                getCurrentUser()
                $('.save-data').attr('hidden', true)
                $('.edit-data').text('EDIT')
                $('.form-control-plaintext').attr('readonly', true).toggleClass('alert-secondary', false)
                editable = true
            }
        }

        function getNextUsers() {
            $.ajax({
                type: 'GET',
                url: `${host}/users/random`,
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: function (data) {
                    insertUser(data[0], 'A')
                    insertUser(data[1], 'B')
                }
            })
        }

        function selectUser(winnerId) {
            let aId = $('.userA-card').data('userId')
            let bId = $('.userB-card').data('userId')
            let loserId;
            if (winnerId === aId) {
                loserId = bId
            } else {
                loserId = aId
            }
            const formData = {
                winnerId: winnerId,
                loserId: loserId,
            }
            $.ajax({
                type: 'POST',
                url: `${host}/users/${formData.winnerId}/win/${formData.loserId}`,
                data: JSON.stringify(formData),
                contentType: 'application/json; charset=utf-8',
                success: function (data) {
                    resetRating()
                }
            })
        }

        function insertUser(user, type) {
            let card = $(`.user${type}-card`)
            card.find('.card-title').text(user.username)
            card.find('.card-text').text(user.description)
            card.data('userId', user.id)
        }

        function fillButtons(userId) {
            return `<div class="dropdown dropright">
            <a class="btn btn-sm btn-secondary dropdown-toggle" role="button" id="dropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                USER
            </a>
            <div class="dropdown-menu admin-menu" aria-labelledby="dropdownMenuLink">
                <button type="button" class="dropdown-item block-${userId}" id="block-block-block">BLOCK</button>
                <button type="button" class="dropdown-item unblock-${userId}">UNBLOCK</button>
                <button type="button" class="dropdown-item delete-${userId}">DELETE</button>
                <button type="button" class="dropdown-item logs-${userId}">LOGS</button>
            </div>
        </div>`
        }

        function getUsersByRating() {
            $.ajax({
                type: 'GET',
                url: `${host}/users/top`,
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: function (data) {
                    const text = ['text-danger', 'text-warning', 'text-success', 'text-primary', 'text-info', 'text-secondary']
                    data.forEach(function (item, i) {
                        let userId = item.id
                        let button
                        if (isAdmin) {
                            button = fillButtons(userId)
                        } else button = ''
                        if (i <= 4) {
                            addCard(text[i], item, button)
                        } else {
                            addCard(text[5], item, button)
                        }
                        $(`.block-${userId}`).click(function () {
                            blockUser(userId)
                        })
                        $(`.unblock-${userId}`).click(function () {
                            unblockUser(userId)
                        })
                        $(`.delete-${userId}`).click(function () {
                            deleteUser(userId)
                        })
                        $(`.logs-${userId}`).click(function () {
                            viewLogs(userId)
                        })
                    })
                }
            })
        }

        function addCard(text, user, button) {
            $('#bests').closest('.tab-pane').append('\n' +
                '            <div class="card border-primary mb-3 top-user" style="max-width: 18rem">\n' +
                `                <div class="card-header">
                                     <div class="row">
                                         <div class="col-md-9">${user.rating}</div>${button}
                                     </div></div>\n` +
                `                <div class="card-body ${text}">\n` +
                `                    <h5 class="card-title">${user.username}</h5>\n` +
                `                    <p class="card-text">${user.description}</p>\n` +
                '                </div>\n' +
                '            </div>')
        }

        function resetRating() {
            $('.top-user').remove()
            getUsersByRating()
        }

        function getCurrentUser() {
            $.ajax({
                type: 'GET',
                url: `${host}/users/current`,
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: function (data) {
                    if (data.roles.some(r => r.roleName === 'ADMIN')) {
                        $('#creation').closest('.creation').removeAttr('hidden')
                        isAdmin = true
                    } else {
                        isAdmin = false
                    }
                    $('.current-user').addClass('text-primary')
                    $('#current').closest('.current-user').append(`<a href="/personal">${data.username}</a>`)
                    $('#login-logout').text('Log out')
                    let button = $(`.login`)
                    button.data('isAuth', 'true')
                    $('#change-username').closest('.form-control-plaintext').val(data.username)
                    $('#change-description').closest('.form-control-plaintext').val(data.description)
                    $('#change-email').closest('.form-control-plaintext').val(data.email)
                    $('.page-name').text(data.username)
                    $('.page-info').text(data.description)
                    let container = $('.wrapper')
                    container.data('userId', data.id)
                },
                error: function (error) {
                    $('#current').closest('.current-user').text('Not authorized')
                }
            })
        }

        function changeUsersData() {
            const formData = {
                id: $('.wrapper').data('userId'),
                username: $('#change-username').val(),
                description: $('#change-description').val(),
                email: $('#change-email').val()
            }
            $.ajax({
                type: 'PUT',
                url: `${host}/users/update`,
                data: JSON.stringify(formData),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: function (data) {
                    toggleButtons()
                    $('.toast').toast("show")
                },
                error: function (errorMsg) {
                    errorMsg.responseJSON.forEach(error => {
                        let field = '#change-' + error.field
                        $(field).closest('.col-sm-7').append(`<div class="invalid-feedback">${error.field} ${error.message}</div>`)
                        $(field).addClass('is-invalid')
                        $(field).val('')
                    })
                }
            })
        }

        function removeMessages() {
            $('.form-message').empty()
            $('.invalid-feedback').remove()
            $('.is-invalid').removeClass('is-invalid')
        }

        function changePassword() {
            const formData = {
                id: $('.wrapper').data('userId'),
                oldPassword: $('#old-password').val(),
                newPassword: $('#new-password').val()
            }
            $.ajax({
                type: 'POST',
                url: `${host}/users/password`,
                data: JSON.stringify(formData),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: function (data) {
                    $('.toast').toast("show")
                    removeMessages()
                },
                error: function (errorMsg) {
                    let messages = 'Sorry, there are the errors: '
                    errorMsg.responseJSON.forEach(error => {
                        messages = messages + error.message + ' '
                    })
                    $('.toast').text(`${messages}`).toast('show')

                }
            })
        }

        function blockUser(userId) {
            $.ajax({
                type: 'POST',
                url: `${host}/accounts/block/${userId}`,
                contentType: 'application/json; charset=utf-8',
                success: function (data) {
                }
            })
        }

        function unblockUser(userId) {
            $.ajax({
                type: 'POST',
                url: `${host}/accounts/unblock/${userId}`,
                contentType: 'application/json; charset=utf-8',
                success: function (data) {
                }
            })
        }

        function deleteUser(userId) {
            $.ajax({
                type: 'DELETE',
                url: `${host}/accounts/${userId}`,
                contentType: 'application/json; charset=utf-8',
                success: function (data) {
                    getUsersByRating()
                }
            })
        }

        function viewLogs(userId) {

        }
    })
