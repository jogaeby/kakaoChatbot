$(document).ready(function(){
    const headers = [
        'No', '제목','설명', '작성자','상태','등록일','기타'
    ];

    const jwtToken = getCookie('session-id');
    const payload = parseJWT(jwtToken);
    const userRole = payload.role;
    const id = payload.id;

    getProducts()

    $("#addProductModalButton").on("click",function () {
        $('#addModal').find('input').val('');
        $('#addModal').find('textarea').val('');
        $('#addModal').modal('show');
    })

    $("#addProductButton").on("click",function () {
        const imageUrl = $("#addProductImageUrl").val();
        const title = $("#addProductTitle").val();
        const description = $("#addProductDescription").val();
        const link = $("#addProductLink").val();

        if (!imageUrl) {
            alert("이미지는 필수입니다.")
            return
        }
        if (!description) {
            alert("설명은 필수입니다.")
            return
        }
        addProduct(imageUrl,title,description,link)
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


    function addProduct(imageUrl,title,description,link) {
        const data = JSON.stringify({
            images:[imageUrl],
            title:title,
            description:description,
            link:link,
        });

        fetch(`/product`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body:data
        })
            .then(response => {
                if (response.ok){
                    alert("성공적으로 추가하였습니다.")
                    $('#addModal').modal('hide');
                    getProducts()
                }else {
                    alert("추가를 실패하였습니다.")
                }

            })
            .catch(error => {
                console.log(error)
            });
    }

    function getProducts() {
        const page = 0
        const size = 20
        const sort = 'createDate,desc'

        fetch(`/product/list?page=${page}&size=${size}&sort=${sort}`, {
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
        const row = $('<tr>');
        row.append($('<td>').text(index + 1));
        const idCell = $('<td>').text(data.title).css({ cursor: 'pointer', color: 'blue' });

        idCell.on('click', () => {
            const isOwner = (userRole == '관리자' || id == data.memberId);

            $('#updateProductButton').toggle(isOwner);

            // 모달에 데이터 채우기
            $('#updateProductId').val(data.id);
            $('#updateProductImageUrl').val(data.images);
            $('#updateProductTitle').val(data.title);
            $('#updateProductDescription').val(data.description);
            $('#updateProductLink').val(data.link);
            $('#updateProductMemberId').val(data.memberId);

            $('#updateProductImageUrl').prop('readonly', !isOwner);
            $('#updateProductTitle').prop('readonly', !isOwner);
            $('#updateProductDescription').prop('readonly', !isOwner);
            $('#updateProductLink').prop('readonly', !isOwner);

            // 모달 열기
            $('#updateModal').modal('show');
        });

        row.append(idCell);
        row.append($('<td>').text(data.description));
        row.append($('<td>').text(data.memberId));
        row.append($('<td>').text(data.status));
        row.append($('<td>').text(data.createDate));

        if (userRole == '관리자' || id == data.memberId) {
            row.append($('<td style="display: flex; justify-content: center;">').append(deleteButton(data)));
        }else {
            row.append($('<td>').append($('<td>').text("")));
        }

        return row;
    }

    function deleteButton (data) {
        return  $('<button>').text('삭제').addClass('btn btn-danger mx-lg-1 btn-sm').on('click',function (){
            if (confirm(`${data.title}을 삭제하시겠습니까?`)) {
                deleteMember(data.id)
            }
        })
    }

    function deleteMember(id) {
        fetch(`/product/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (response.ok){
                    alert("성공적으로 삭제하였습니다.")
                    getProducts()
                }else {
                    alert("삭제를 실패하였습니다.")
                }
            })
            .catch(error => {
                console.log(error)
            });
    }
})


