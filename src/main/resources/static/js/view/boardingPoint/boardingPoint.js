$(document).ready(function(){
    const headers = [
        'No', '버스명','탐승지','차량번호', '기사 이름','기사 연락처','근무 시작시간','버스 탑승시간','관리'
    ];

    getBoardingPoints()

    $("#addBoardPointButton").on("click",function () {
        $('#addModal').find('input').val('');
        $('#addModal').modal('show');
    })

    $("#addButton").on("click",function () {
        let addBusName = $("#addBusName").val();
        let addBusNumber = $("#addBusNumber").val();
        let addBusDriverName = $("#addBusDriverName").val();
        let addBusDriverPhone = $("#addBusDriverPhone").val();
        let addBoardPoint = $("#addBoardPoint").val();
        let addStartWorkTime = $("#addStartWorkTime").val();
        let addDepartBusTime = $("#addDepartBusTime").val();

        if (!addBusName) {
            alert("버스 이름을 입력해주세요.")
            return
        }

        if (!addBusNumber) {
            alert("버스 차량번호를 입력해주세요.")
            return
        }

        if (!addBusDriverName) {
            alert("기사 이름을 입력해주세요.")
            return
        }

        if (!addBusDriverPhone) {
            alert("기사 연락처를 입력해주세요.")
            return
        }

        if (!addBoardPoint) {
            alert("버스 탑승지를 입력해주세요.")
            return
        }

        if (!addStartWorkTime) {
            alert("근무 시작시간을 입력해주세요.")
            return
        }

        if (!addDepartBusTime) {
            alert("버스 탑승시간을 입력해주세요.")
            return
        }

        addBoardingPoint(addBusName,addBusNumber,addBusDriverName,addBusDriverPhone,addBoardPoint,addStartWorkTime,addDepartBusTime)
    })


    $("#updateButton").on("click",function () {
        let id = $("#updateBoardPointId").val();
        let busName = $("#updateBusName").val();
        let busNumber = $("#updateBusNumber").val();
        let busDriverName = $("#updateBusDriverName").val();
        let busDriverPhone = $("#updateBusDriverPhone").val();
        let boardPoint = $("#updateBoardPoint").val();
        let workTime = $("#updateStartWorkTime").val();
        let busTime = $("#updateDepartBusTime").val();

        if (!busName) {
            alert("버스 이름을 입력해주세요.")
            return
        }

        if (!busNumber) {
            alert("버스 차량번호를 입력해주세요.")
            return
        }

        if (!busDriverName) {
            alert("기사 이름을 입력해주세요.")
            return
        }

        if (!busDriverPhone) {
            alert("기사 연락처를 입력해주세요.")
            return
        }

        if (!boardPoint) {
            alert("버스 탑승지를 입력해주세요.")
            return
        }

        if (!workTime) {
            alert("근무 시작시간을 입력해주세요.")
            return
        }

        if (!busTime) {
            alert("버스 탑승시간을 입력해주세요.")
            return
        }

        updateMember(id,busName,busNumber,busDriverName,busDriverPhone,boardPoint,workTime,busTime)
    })

    function updateMember(id,busName,busNumber,busDriverName,busDriverPhone,boardPoint,workTime,busTime) {
        const addData = JSON.stringify({
            id:id,
            busName:busName,
            busNumber:busNumber,
            driverName:busDriverName,
            driverPhone:busDriverPhone,
            boardPoint:boardPoint,
            startWorkTime:workTime,
            departBusTime:busTime
        });

        fetch(`/boardingPoint`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body:addData
        })
            .then(response => {
                if (response.ok){
                    alert("성공적으로 수정하였습니다.")
                    $('#updateModal').modal('hide');
                    getBoardingPoints()
                }else {
                    alert("수정을 실패하였습니다.")
                }

            })
            .catch(error => {
                console.log(error)
            });
    }


    function addBoardingPoint(addBusName,addBusNumber,addBusDriverName,addBusDriverPhone,addBoardPoint,addStartWorkTime,addDepartBusTime) {
        const addData = JSON.stringify({
            busName:addBusName,
            busNumber:addBusNumber,
            driverName:addBusDriverName,
            driverPhone:addBusDriverPhone,
            boardPoint:addBoardPoint,
            startWorkTime:addStartWorkTime,
            departBusTime:addDepartBusTime
        });

        fetch(`/boardingPoint`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body:addData
        })
            .then(response => {
                if (response.ok){
                    alert("성공적으로 추가하였습니다.")
                    $('#addModal').modal('hide');
                    getBoardingPoints()
                }else {
                    alert("추가를 실패하였습니다.")
                }

            })
            .catch(error => {
                console.log(error)
            });
    }

    function getBoardingPoints() {
        fetch(`/boardingPoint/list`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                return response.json()
            }).then(data => {
            renderTable(data,headers,createTableRow)
        })
            .catch(error => {
                console.log(error)
            });
    }

    function createTableRow(data, index) {
        const idCell = $('<td>').text(data.busName).css({ cursor: 'pointer', color: 'blue' });
        idCell.on('click', () => {
            // 모달에 데이터 채우기
            $('#updateBoardPointId').val(data.id);
            $('#updateBusName').val(data.busName);
            $('#updateBusNumber').val(data.busNumber);
            $('#updateBusDriverName').val(data.driverName);
            $('#updateBusDriverPhone').val(data.driverPhone);
            $('#updateBoardPoint').val(data.boardPoint);
            $('#updateStartWorkTime').val(data.startWorkTime);
            $('#updateDepartBusTime').val(data.departBusTime);
            // 모달 열기
            $('#updateModal').modal('show');
        });

        const row = $('<tr>');
        row.append($('<td>').text(index + 1));
        row.append(idCell);
        row.append($('<td>').text(data.boardPoint));
        row.append($('<td>').text(data.busNumber));
        row.append($('<td>').text(data.driverName));
        row.append($('<td>').text(data.driverPhone));
        row.append($('<td>').text(data.startWorkTime));
        row.append($('<td>').text(data.departBusTime));
        row.append($('<td style="display: flex; justify-content: center;">').append(deleteButton(data)));

        return row;
    }

    function deleteButton (data) {
        return  $('<button>').text('삭제').addClass('btn btn-danger mx-lg-1 btn-sm').on('click',function (){

            if (confirm(`${data.busName}을 삭제하시겠습니까?`)) {
                deleteBoardingPoint(data.id)
            }
        })
    }

    function deleteBoardingPoint(id) {
        fetch(`/boardingPoint/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (response.ok){
                    alert("성공적으로 삭제하였습니다.")
                    getBoardingPoints()
                }else {
                    alert("삭제를 실패하였습니다.")
                }
            })
            .catch(error => {
                console.log(error)
            });
    }
})


