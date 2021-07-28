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
            window.location = '/creationForm'
        })

        function getNextUsers() {
            $.ajax({
                type: 'GET',
                url: 'http://localhost:8080/users/random',
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
                url: `http://localhost:8080/users/${formData.winnerId}/win/${formData.loserId}`,
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

        function getUsersByRating() {
            $.ajax({
                type: 'GET',
                url: 'http://localhost:8080/users/getByRating',
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: function (data) {
                    const text = ['text-danger', 'text-warning', 'text-success', 'text-primary', 'text-info', 'text-secondary']
                    data.forEach(function (item, i, data) {
                        if (i <= 4) {
                            addCard(text[i], item)
                        } else {
                            addCard(text[5], item)
                        }
                    })
                }
            })
        }

        function addCard(text, user) {
            $('#bests').closest('.tab-pane').append('\n' +
                '            <div class="card border-primary mb-3 top-user" style="max-width: 18rem">\n' +
                `                <div class="card-header">${user.rating}</div>\n` +
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
                url: 'http://localhost:8080/users/current',
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: function (data) {
                    if (data.roles.some(r => r.role === 'ADMIN')) {
                        $('#creation').closest('.creation').removeAttr('hidden')
                    }
                    $('.current-user').addClass('text-primary')
                    $('#current').closest('.current-user').text(data.username)
                    $('#login-logout').text('Log out')
                    let button = $(`.login`)
                    button.data('isAuth', 'true')
                },
                error: function (error) {
                    $('#current').closest('.current-user').text('Not authorized')
                }
            })
        }
    })
