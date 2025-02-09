$(document).ready(function(){
    const headers = [
        'No', '제목', '작성자','상태','등록일','기타'
    ];

    const jwtToken = getCookie('session-id');
    const payload = parseJWT(jwtToken);
    const userRole = payload.role;
    const id = payload.id;

    getProducts()

    $("#addProductModalButton").on("click",function () {
        $('#addModal').find('input').val('');
        $('#addModal').modal('show');
    })

    $("#addProductButton").on("click",function () {
        const imageUrl = $("#addProductImageUrl").val();
        const title = $("#addProductTitle").val();
        const no = $("#addProductNo").val();
        const category = $("#addProductCategory").val();
        const location = $("#addProductLocation").val();
        const price = $("#addProductPrice").val();
        const minPrice = $("#addProductMinPrice").val();
        const expectedPrice = $("#addProductExpectedPrice").val();
        const saleDate = $("#addProductSaleDate").val();
        const managerName = $("#addProductManagerName").val();
        const link = $("#addProductLink").val();

        if (!imageUrl) {
            alert("이미지 URL을 입력하세요.")
            return
        }

        if (!title) {
            alert("제목을 입력하세요.")
            return
        }

        if (!no) {
            alert("타경번호를 입력하세요.")
            return
        }

        if (!category) {
            alert("물건종류를 입력하세요.")
            return
        }
        if (!location) {
            alert("소재지를 입력하세요.")
            return
        }
        if (!price) {
            alert("감정가를 입력하세요.")
            return
        }
        if (!minPrice) {
            alert("최저가를 입력하세요.")
            return
        }
        if (!expectedPrice) {
            alert("예상 낙찰가를 입력하세요.")
            return
        }

        if (!saleDate) {
            alert("매각 기일을 선택하세요.")
            return
        }
        if (!managerName) {
            alert("담당자 이름을 입력하세요.")
            return
        }

        if (!link) {
            alert("링크를 입력하세요.")
            return
        }

        addProduct(imageUrl,title,no, category, location, price, minPrice, expectedPrice, saleDate, managerName, link)
    })

    $("#updateProductButton").on("click",function () {
        const productId = $('#updateProductId').val();
        const imageUrl = $('#updateProductImageUrl').val();
        const title = $('#updateProductTitle').val();
        const no = $('#updateProductNo').val();
        const category = $('#updateProductCategory').val();
        const location = $('#updateProductLocation').val();
        const price = $('#updateProductPrice').val();
        const minPrice = $('#updateProductMinPrice').val();
        const expectedPrice = $('#updateProductExpectedPrice').val();
        const saleDate = $('#updateProductSaleDate').val();
        const managerName = $('#updateProductManagerName').val();

        const link = $('#updateProductLink').val();
        const memberId = $('#updateProductMemberId').val();

        if (!productId || !memberId) {
            alert("관리자에게 문의하세요")
            return
        }

        if (!imageUrl) {
            alert("이미지 URL을 입력하세요.")
            return
        }

        if (!title) {
            alert("제목을 입력하세요.")
            return
        }

        if (!no) {
            alert("타경번호를 입력하세요.")
            return
        }

        if (!category) {
            alert("물건종류를 입력하세요.")
            return
        }
        if (!location) {
            alert("소재지를 입력하세요.")
            return
        }
        if (!price) {
            alert("감정가를 입력하세요.")
            return
        }
        if (!minPrice) {
            alert("최저가를 입력하세요.")
            return
        }
        if (!expectedPrice) {
            alert("예상 낙찰가를 입력하세요.")
            return
        }

        if (!saleDate) {
            alert("매각 기일을 선택하세요.")
            return
        }
        if (!managerName) {
            alert("담당자 이름을 입력하세요.")
            return
        }

        if (!link) {
            alert("링크를 입력하세요.")
            return
        }

        updateProduct(productId,imageUrl,title,no, category, location, price, minPrice, expectedPrice, saleDate, managerName, link,memberId)
    })

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

            $('#updateProductNo').val(data.no);
            $('#updateProductCategory').val(data.category);
            $('#updateProductLocation').val(data.location);
            $('#updateProductPrice').val(data.price);
            $('#updateProductMinPrice').val(data.minPrice);
            $('#updateProductExpectedPrice').val(data.expectedPrice);
            $('#updateProductSaleDate').val(data.saleDate);
            $('#updateProductManagerName').val(data.managerName);

            $('#updateProductLink').val(data.link);
            $('#updateProductMemberId').val(data.memberId);

            $('#updateProductImageUrl').prop('readonly', !isOwner);
            $('#updateProductTitle').prop('readonly', !isOwner);
            $('#updateProductNo').prop('readonly', !isOwner);
            $('#updateProductCategory').prop('readonly', !isOwner);
            $('#updateProductLocation').prop('readonly', !isOwner);
            $('#updateProductPrice').prop('readonly', !isOwner);
            $('#updateProductMinPrice').prop('readonly', !isOwner);
            $('#updateProductExpectedPrice').prop('readonly', !isOwner);
            $('#updateProductSaleDate').prop('readonly', !isOwner);
            $('#updateProductManagerName').prop('readonly', !isOwner);
            $('#updateProductLink').prop('readonly', !isOwner);

            // 모달 열기
            $('#updateModal').modal('show');
        });

        row.append(idCell);
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

    function updateProduct(productId,imageUrl,title,no, category, location, price, minPrice, expectedPrice, saleDate, managerName, link,memberId) {
        const data = JSON.stringify({
            id:productId,
            images:[imageUrl],
            title:title,
            no:no,
            category:category,
            location:location,
            price:price,
            minPrice:minPrice,
            expectedPrice:expectedPrice,
            saleDate:saleDate,
            managerName:managerName,
            link:link,
            memberId:memberId
        });

        fetch(`/product`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body:data
        })
            .then(response => {
                if (response.ok){
                    alert("성공적으로 수정하였습니다.")
                    $('#updateModal').modal('hide');
                    getProducts()
                }else {
                    alert("수정을 실패하였습니다.")
                }

            })
            .catch(error => {
                console.log(error)
            });
    }

    function addProduct(imageUrl,title,no, category, location, price, minPrice, expectedPrice, saleDate, managerName, link) {
        const data = JSON.stringify({
            images:[imageUrl],
            title:title,
            no:no,
            category:category,
            location:location,
            price:price,
            minPrice:minPrice,
            expectedPrice:expectedPrice,
            saleDate:saleDate,
            managerName:managerName,
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

    function deleteButton (data) {
        return  $('<button>').text('삭제').addClass('btn btn-danger mx-lg-1 btn-sm').on('click',function (){
            if (confirm(`${data.title}을 삭제하시겠습니까?`)) {
                deleteProduct(data.id)
            }
        })
    }

    function deleteProduct(id) {
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


