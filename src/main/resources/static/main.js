$(document).ready(
    function () {
        getNextUsers()
        $("#userForm").submit(function (event) {
            event.preventDefault()
            removeMessages()
            userPost()
        })
        $(".card-dtn").click(function (event) {
            let winnerId = $(this).closest(".card").data("userId")
            selectUser(winnerId)
            getNextUsers()
        })

        function userPost() {
            const formData = {
                userName: $("#userName").val(),
                description: $("#description").val(),
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
            $(":input", "#userForm").not(":button, :submit, :reset, :hidden").val('')
                .removeAttr('checked')
                .removeAttr('selected')
        }

        function removeMessages() {
            $(".form-message").empty()
            $(".invalid-feedback").remove()
            $(".is-invalid").removeClass("is-invalid")
        }

        function getNextUsers() {
            $.ajax({
                type: "GET",
                url: "http://localhost:8080/users/random",
                contentType: "application/json; charset=utf-8",
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
            let aId = $(".userA-card").data("userId")
            let bId = $(".userB-card").data("userId")
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
                    type: "POST",
                    url: "http://localhost:8080/users/" + formData.winnerId + "/win/" + formData.loserId,
                    data: JSON.stringify(formData),
                    contentType: "application/json; charset=utf-8",
                    success: function (data) {
                        console.log('success')
                    },
                    error: function (errMsg) {
                        console.log('error')
                    }
                })
        }

        function insertUser(user, type) {
            let card = $('.user' + type + '-card')
            $(card).find(".card-title").text(user.userName)
            $(card).find(".card-text").text(user.description)
            $(card).data("userId", user.id)
        }
    })
