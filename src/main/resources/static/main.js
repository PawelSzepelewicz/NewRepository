$(document).ready(
    function () {
        getUsersByRating()
        getNextUsers()
        $('#userForm').submit(function (event) {
            event.preventDefault()
            removeMessages()
            userPost()
        })
        $('.card-dtn').click(function (event) {
            let winnerId = $(this).closest('.card').data('userId')
            selectUser(winnerId)
            getNextUsers()
        })

        function userPost() {
            const formData = {
                userName: $('#userName').val(),
                description: $('#description').val(),
            }
            $.ajax({
                type: 'POST',
                url: 'http://localhost:8080/users',
                data: JSON.stringify(formData),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: function (data) {
                    resetData()
                    $('.form-message').text('User has been created successfully.')
                    resetRating()
                },
                error: function (errMsg) {
                    errMsg.responseJSON.forEach(error => {
                        let field = '#' + error.field
                        $(field).closest('.form-group').append(`<di class="invalid-feedback">${error.field} ${error.message}</div>`)
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

        function getNextUsers() {
            $.ajax({
                type: 'GET',
                url: 'http://localhost:8080/users/random',
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: function (data) {
                    console.log(data)
                    insertUser(data[0], 'A')
                    insertUser(data[1], 'B')
                },
                error: function (errMsg) {
                    console.log('random error')
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
                    console.log('success')
                    resetRating()
                },
                error: function (errMsg) {
                    console.log('error')
                }
            })
        }

        function insertUser(user, type) {
            let card = $(`.user${type}-card`)
            card.find('.card-title').text(user.userName)
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
                        console.log(data)
                    })
                },
                error: function (errMsg) {
                    console.log('random error')
                }
            })
        }

        function addCard(text, user) {
            $('#bests').closest('.tab-pane').append('\n' +
                '            <div class="card border-primary mb-3 top-user" style="max-width: 18rem;">\n' +
                `                <div class="card-header">${user.rating}</div>\n` +
                `                <div class="card-body ${text}">\n` +
                `                    <h5 class="card-title">${user.userName}</h5>\n` +
                `                    <p class="card-text">${user.description}</p>\n` +
                '                </div>\n' +
                '            </div>')
        }

        function resetRating() {
            $('.top-user').remove()
            getUsersByRating()
        }
    })
