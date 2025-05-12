$(document).ready(function() {
    const headers = [
        'No', '이름', '연락처', '성별', '연령대', '지점명','룸투어 희망일','거주기간','입주 예정일','주소','호수','상태','접수일', '기타'
    ];
    const sortableColumns = ["매각 기일", "등록일"];

    const jwtToken = getCookie('session-id');
    const payload = parseJWT(jwtToken);
    const userRole = payload.role;
    const id = payload.id;

    getData()

    renderCategories()

    $('#searchDate').on('change', function () {
        const selectedDate = $(this).val();

        searchProducts(selectedDate,"visitDate")
    });

    $("#searchButton").on("click", function () {
        const searchInput = $('#searchInput').val();
        const category = $('#categorySelect').val();

        if (category && !searchInput) {
            alert("검색어를 입력하세요.")
            return
        }
        if (!category && searchInput) {
            alert("카테고리를 선택하세요.")
            return
        }

        if (!category && !searchInput) {
            getData()
            return;
        }

        searchProducts(searchInput, category)
    })

    $("#addProductButton").on("click", function () {
        const id = $("#addReservationId").val();
        const address = $("#addAddress").val();
        const roomNumber = $("#addRoomNumber").val();

        if (!address) {
            alert("주소를 입력하세요.")
            return;
        }
        if (!roomNumber) {
            alert("호수를 입력하세요.")
            return;
        }

        addRoomNumber(id,address,roomNumber)
    })


    function searchProducts(searchInput, searchCategory) {
        fetch(`/reservation/roomTour/search?input=${searchInput}&category=${searchCategory}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                return response.json()
            }).then(data => {
            renderTable(data, headers, createTableRow, sortableColumns)
        })
            .catch(error => {
                console.log(error)
            });
    }

    function getData() {
        const page = 0
        const size = 20
        const sort = 'createDate,desc'

        fetch(`/reservation/roomTour/list?page=${page}&size=${size}&sort=${sort}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                return response.json()
            }).then(data => {
            renderTable(data, headers, createTableRow, sortableColumns)
        })
            .catch(error => {
                console.log(error)
            });
    }

    function addRoomNumber(id,address,roomNumber) {
        const payload = {
            id: id,
            address: address,
            roomNumber: roomNumber
        };
        $('#loadingOverlay').show();
        fetch(`/reservation/roomTour/roomNumber`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        })
            .then(response => {
                $('#loadingOverlay').hide();
                if (!response.ok) {
                    alert("방 번호 배정에 실패하였습니다.");
                    return;
                }
                alert("방 번호가 성공적으로 배정되었습니다.");
                $('#addModal').modal('hide');
                getData()
            })
            .catch(error => {
                $('#loadingOverlay').hide();
                console.log(error)
            });
    }

    function createTableRow(data, index) {
        console.log(data)
        'No', '이름', '연락처', '성별', '연령대', '지점명','룸투어 희망일','거주기간','입주 예정일','접수일', '기타'
        const formattedCreateDate = formatDateTime(data.createDate);
        const row = $(`<tr id = ${data.id}>`);
        //
        // const idCell = $('<td data-column="Memo">').text(data.studentName)
        //     .css({cursor: 'pointer', color: 'blue'});
        // idCell.on('click', () => openUpdateModal(data));
        //
        // row.append(idCell);

        row.append($('<td data-column="idx">').text(index + 1));
        row.append($('<td data-column="name">').text(data.name));
        row.append($('<td data-column="phone">').text(data.phone));
        row.append($('<td data-column="gender">').text(data.gender));
        row.append($('<td data-column="age">').text(data.age));
        row.append($('<td data-column="location">').text(data.location));
        row.append($('<td data-column="visitDate">').text(formatDateTime(data.visitDate)));
        row.append($('<td data-column="period">').text(data.period));
        row.append($('<td data-column="moveInDate">').text(data.moveInDate));
        row.append($('<td data-column="address">').text(data.address));
        row.append($('<td data-column="roomNumber">').text(data.roomNumber));
        row.append($('<td data-column="status">').text(data.status));
        row.append($('<td data-column="createDate">').text(formatDateTime(data.createDate)));
        const actionsCell = $('<td style="display: flex; justify-content: center;" data-column="Actions">');
        if (data.status == "접수") {
            actionsCell.append(assignmentButton(data)); // 기타 버튼 함수
        }


        if (userRole == '관리자' || id == data.memberId) {
            actionsCell.append($('<td style="display: flex; justify-content: center;" data-column="Actions">').append(deleteButton(data)));
        } else {
            actionsCell.append($('<td data-column="Actions">').text(""));
        }

        row.append(actionsCell.length > 0 ? actionsCell : $('<td data-column="Actions">').text(""));
        return row;
    }

    function deleteButton(data) {
        return $('<button>').text('삭제').addClass('btn btn-danger mx-lg-1 btn-sm').on('click', function () {
            if (confirm(`예약을 삭제할까요?`)) {
                deleteProduct(data.id)
            }
        })
    }

    function assignmentButton(data) {
        return $('<button>').text('배정').addClass('btn btn-primary mx-lg-1 btn-sm').on('click', function () {
            $("#addReservationId").val(data.id);
            $("#addAddress").val("");
            $("#addRoomNumber").val("");
            $('#addModal').modal('show');
        })
    }

    function deleteProduct(id) {
        fetch(`/reservation/roomTour/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (response.ok) {
                    alert("예약이 성공적으로 취소되었습니다.")
                    getData()
                } else {
                    alert("예약 취소를 실패하였습니다.")
                }
            })
            .catch(error => {
                console.log(error)
            });
    }
})


