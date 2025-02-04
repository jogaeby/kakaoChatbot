$(document).ready(function(){
    const headers = [
        'No', '개인키','이름', '연락처','버스 탑승지','성별','생년월일','주소','가입일','관리'
    ];
    getMembers()

    getBoardPoints();

    $("#addMemberButton").on("click",function () {
        $('#addModal').find('input').val('');
        $('#addModal').modal('show');
    })

    $("#addMember").on("click",function () {
        let name = $("#addMemberName").val();
        let phone = $("#addMemberPhone").val();
        let gender = $("#addMemberGender").val();
        let birthDate = $("#addMemberBirthDate").val();
        let address = $("#addMemberAddress").val();
        let kakaoUserKey = $("#addMemberKakaoUserKey").val();
        let addMemberBoardPoint = $("#addMemberBoardPoint").val();
        let addMemberGroupName = $("#addMemberGroupName").val();
        if (!name) {
            alert("이름을 입력하세요.")
            return
        }

        if (!phone) {
            alert("연락처를 입력하세요.")
            return
        }

        if (!gender) {
            alert("성별을 선택하세요.")
            return
        }

        if (!birthDate) {
            alert("생년월일을 입력하세요.")
            return
        }

        if (!address) {
            alert("주소를 입력하세요.")
            return
        }

        if (!kakaoUserKey) {
            alert("개인코드를 입력하세요.")
            return
        }

        if (!addMemberBoardPoint) {
            alert("버스 탑승지를 선택하세요.")
            return
        }

        if (!addMemberGroupName) {
            alert("소속을 입력해주세요.")
            return
        }

        addMember(name,phone,gender,birthDate,address,kakaoUserKey,addMemberBoardPoint,addMemberGroupName)
    })


    $("#updateMember").on("click",function () {
        let id = $("#updateMemberId").val();
        let name = $("#updateMemberName").val();
        let phone = $("#updateMemberPhone").val();
        let gender = $("#updateMemberGender").val();
        let birthDate = $("#updateMemberBirthDate").val();
        let address = $("#updateMemberAddress").val();
        let kakaoUserKey = $("#updateMemberKakaoUserKey").val();
        let updateMemberBoardPoint = $("#updateMemberBoardPoint").val();
        let updateMemberGroupName = $("#updateMemberGroupName").val();

        if (!name) {
            alert("이름을 입력하세요.")
            return
        }

        if (!phone) {
            alert("연락처를 입력하세요.")
            return
        }

        if (!gender) {
            alert("성별을 선택하세요.")
            return
        }

        if (!birthDate) {
            alert("생년월일을 입력하세요.")
            return
        }

        if (!address) {
            alert("주소를 입력하세요.")
            return
        }

        if (!kakaoUserKey) {
            alert("개인코드를 입력하세요.")
            return
        }

        if (!updateMemberBoardPoint) {
            alert("버스 탑승지를 선택하세요.")
            return
        }

        if (!updateMemberGroupName) {
            alert("소속을 입력하세요.")
            return
        }

        updateMember(id,name,phone,gender,birthDate,address,kakaoUserKey,updateMemberBoardPoint,updateMemberGroupName)
    })

    function updateMember(id,name,phone,gender,birthDate,address,kakaoUserKey,updateMemberBoardPoint,updateMemberGroupName) {
        const addData = JSON.stringify({
            id:id,
            name:name,
            phone:phone,
            gender:gender,
            birthDate:birthDate,
            address:address,
            kakaoUserKey:kakaoUserKey,
            boardingPointId:updateMemberBoardPoint,
            groupName:updateMemberGroupName
        });

        fetch(`/managers`, {
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
                    getMembers()
                }else {
                    alert("수정을 실패하였습니다.")
                }

            })
            .catch(error => {
                console.log(error)
            });
    }


    function addMember(name,phone,gender,birthDate,address,kakaoUserKey,addMemberBoardPoint,addMemberGroupName) {
        const addData = JSON.stringify({
            name:name,
            phone:phone,
            gender:gender,
            birthDate:birthDate,
            address:address,
            kakaoUserKey:kakaoUserKey,
            boardingPointId:addMemberBoardPoint,
            groupName:addMemberGroupName
        });

        fetch(`/members`, {
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
                    getMembers()
                }else {
                    alert("추가를 실패하였습니다.")
                }

            })
            .catch(error => {
                console.log(error)
            });
    }

    function getMembers() {
        fetch(`/members/list`, {
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
        const idCell = $('<td>').text(data.kakaoUserKey).css({ cursor: 'pointer', color: 'blue' });

        idCell.on('click', () => {
            // 모달에 데이터 채우기
            $('#updateMemberId').val(data.id);
            $('#updateMemberAccountName').val(data.accountId);
            $('#updateMemberName').val(data.name);
            $('#updateMemberPhone').val(data.phone);
            $('#updateMemberGender').val(data.gender);
            $('#updateMemberBirthDate').val(data.birthDate);
            $('#updateMemberAddress').val(data.address);
            $('#updateMemberKakaoUserKey').val(data.kakaoUserKey);
            $('#updateMemberBoardPoint').val(data.boardingPointId);
            $('#updateMemberGroupName').val(data.groupName);
            // 모달 열기
            $('#updateModal').modal('show');
        });

        const row = $('<tr>');
        row.append($('<td>').text(index + 1));
        row.append(idCell);
        row.append($('<td>').text(data.name));
        row.append($('<td>').text(data.phone));
        row.append($('<td>').text(data.boardingPointName));
        row.append($('<td>').text(data.gender));
        row.append($('<td>').text(data.birthDate));
        row.append($('<td>').text(data.address));
        row.append($('<td>').text(data.createDate));
        row.append($('<td style="display: flex; justify-content: center;">').append(deleteButton(data)));

        return row;
    }

    function deleteButton (data) {
        return  $('<button>').text('삭제').addClass('btn btn-danger mx-lg-1 btn-sm').on('click',function (){

            if (confirm(`${data.name}님을 삭제하시겠습니까?`)) {
                deleteMember(data.id)
            }
        })
    }

    function deleteMember(id) {
        fetch(`/members/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (response.ok){
                    alert("성공적으로 삭제하였습니다.")
                    getMembers()
                }else {
                    alert("삭제를 실패하였습니다.")
                }
            })
            .catch(error => {
                console.log(error)
            });
    }

    function getBoardPoints() {
        fetch(`/boardingPoint/list`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                return response.json()
            }).then(data => {
            renderBoardPointSelect(data)
        })
            .catch(error => {
                console.log(error)
            });
    }

    function renderBoardPointSelect(data) {
        $('#updateMemberBoardPoint').empty()
        $('#addMemberBoardPoint').empty()

        data.forEach(function (board) {
            let text = board.busName +' - '+board.boardPoint

            $('#addMemberBoardPoint').append(
                $('<option>', { value: board.id, text: text })
            );

            $('#updateMemberBoardPoint').append(
                $('<option>', { value: board.id, text: text })
            );
        })
    }
})


