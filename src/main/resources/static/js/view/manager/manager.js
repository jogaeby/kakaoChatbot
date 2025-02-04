$(document).ready(function(){
    const headers = [
        'No','아이디', '이름', '연락처','성별','생년월일','주소','권한','가입일','관리'
    ];
    getMembers()

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
        let id = $("#addMemberAccountName").val();
        let password = $("#addMemberPassword").val();
        if (!id || !password) {
            alert("아이디와 비밀번호는 필수로 입력해주세요.")
            return
        }
        addMember(id,password,name,phone,gender,birthDate,address,kakaoUserKey)
    })

    $("#updateMember").on("click",function () {
        let id = $("#updateMemberId").val();
        let accountId = $("#updateMemberAccountName").val();
        let password = $("#updateMemberPassword").val();
        let name = $("#updateMemberName").val();
        let phone = $("#updateMemberPhone").val();
        let gender = $("#updateMemberGender").val();
        let birthDate = $("#updateMemberBirthDate").val();
        let address = $("#updateMemberAddress").val();
        let kakaoUserKey = $("#updateMemberKakaoUserKey").val();

        if (!accountId) {
            alert("아이디를 입력해주세요.")
            return
        }
        updateMember(id,accountId,password,name,phone,gender,birthDate,address,kakaoUserKey)
    })

    function addMember(id,password,name,phone,gender,birthDate,address,kakaoUserKey) {
        const addData = JSON.stringify({
            accountId:id,
            password:password,
            name:name,
            phone:phone,
            gender:gender,
            birthDate:birthDate,
            address:address,
            kakaoUserKey:kakaoUserKey
        });

        fetch(`/managers`, {
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
                    $('#addModal').find('input').val('');
                    getMembers()
                }else {
                    alert("추가를 실패하였습니다.")
                }

            })
            .catch(error => {
                console.log(error)
            });
    }
    function updateMember(id,accountId,password,name,phone,gender,birthDate,address,kakaoUserKey) {
        const addData = JSON.stringify({
            id:id,
            accountId:accountId,
            password:password,
            name:name,
            phone:phone,
            gender:gender,
            birthDate:birthDate,
            address:address,
            kakaoUserKey:kakaoUserKey
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

    function getMembers() {
        fetch(`/managers/list`, {
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
        const idCell = $('<td>').text(data.accountId).css({ cursor: 'pointer', color: 'blue' });
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
            // 모달 열기
            $('#updateModal').modal('show');
        });

        const row = $('<tr>');
        row.append($('<td>').text(index + 1));
        row.append(idCell);
        row.append($('<td>').text(data.name));
        row.append($('<td>').text(data.phone));
        row.append($('<td>').text(data.gender));
        row.append($('<td>').text(data.birthDate));
        row.append($('<td>').text(data.address));
        row.append($('<td>').text(data.role));
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
})


