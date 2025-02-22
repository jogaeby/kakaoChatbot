$(document).ready(function(){
    const headers = [
        'No', '메모', '작성자','상태','매각 기일','등록일','기타'
    ];
    const sortableColumns = ["매각 기일", "등록일"];

    const jwtToken = getCookie('session-id');
    const payload = parseJWT(jwtToken);
    const userRole = payload.role;
    const id = payload.id;

    getProducts()
    renderCategories()


    $("#searchButton").on("click",function () {
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
            getProducts()
            return;
        }

        searchProducts(searchInput,category)
    })

    $("#addProductModalButton").on("click",function () {
        $('#addModal').find('input').val('');
        $('#addModal').modal('show');
    })

    $("#addProductButton").on("click",function () {
        const imageUrl = $("#addProductImageUrl").val();
        const memo = $("#addProductMemo").val();
        const no = $("#addProductNo").val();
        const category = $("#addProductCategory").val();
        const location = $("#addProductLocation").val();
        const price = $("#addProductPrice").val();
        const currentPrice = $("#addProductCurrentPrice").val();
        const minPrice = $("#addProductMinPrice").val();
        const expectedPrice = $("#addProductExpectedPrice").val();
        const saleDate = $("#addProductSaleDate").val();
        const managerName = $("#addProductManagerName").val();
        const managerPhone = $("#addProductManagerPhone").val();
        const description = $("#addProductDescription").val();
        const link = $("#addProductLink").val();

        if (!imageUrl) {
            alert("이미지 URL을 입력하세요.")
            return
        }

        if (!memo) {
            alert("메모를 입력하세요.")
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
        if (!currentPrice) {
            alert("현시세를 입력하세요.")
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
        if (!managerPhone) {
            alert("담당자 연락처를 입력하세요.")
            return
        }

        if (!description) {
            alert("장단점을 입력하세요.")
            return
        }
        if (!link) {
            alert("링크를 입력하세요.")
            return
        }

        addProduct(imageUrl,memo,no, category, location, price, currentPrice,minPrice, expectedPrice, saleDate, managerName, managerPhone, description,link)
    })

    $("#updateProductButton").on("click",function () {
        const productId = $('#updateProductId').val();
        const imageUrl = $('#updateProductImageUrl').val();
        const memo = $('#updateProductMemo').val();
        const no = $('#updateProductNo').val();
        const category = $('#updateProductCategory').val();
        const location = $('#updateProductLocation').val();
        const price = $('#updateProductPrice').val();
        const currentPrice = $('#updateProductCurrentPrice').val();
        const minPrice = $('#updateProductMinPrice').val();
        const expectedPrice = $('#updateProductExpectedPrice').val();
        const saleDate = $('#updateProductSaleDate').val();
        const managerName = $('#updateProductManagerName').val();
        const managerPhone = $('#updateProductManagerPhone').val();
        const description = $('#updateProductDescription').val();
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

        if (!memo) {
            alert("메모를 입력하세요.")
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
        if (!currentPrice) {
            alert("현시세를 입력하세요.")
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
        if (!managerPhone) {
            alert("담당자 연락처를 입력하세요.")
            return
        }
        if (!description) {
            alert("장단점을 입력하세요.")
            return
        }
        if (!link) {
            alert("링크를 입력하세요.")
            return
        }

        updateProduct(productId,imageUrl,memo,no, category, location, price, currentPrice, minPrice, expectedPrice, saleDate, managerName, managerPhone, description, link,memberId)
    })

    function searchProducts(searchInput, searchCategory) {
        fetch(`/product/search?input=${searchInput}&category=${searchCategory}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                return response.json()
            }).then(data => {
            renderTable(data, headers, createTableRow,sortableColumns)
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
            renderTable(data,headers,createTableRow,sortableColumns)
        })
            .catch(error => {
                console.log(error)
            });
    }

    function createTableRow(data, index) {
        const row = $(`<tr id = ${data.id}>`);

        row.append($('<td data-column="Index">').text(index + 1));

        const idCell = $('<td data-column="Memo">').text(data.memo)
            .css({ cursor: 'pointer', color: 'blue' });

        idCell.on('click', () => openUpdateModal(data));

        row.append(idCell);
        row.append($('<td data-column="Member ID">').text(data.memberId));
        row.append($('<td data-column="Status">').text(data.status));

        // 날짜 포맷 변경 (YYYY-MM-DD)
        const formattedSaleDate = formatDate(data.saleDate);
        const formattedCreateDate = formatDate(data.createDate);

        row.append($('<td data-column="매각 기일">').text(formattedSaleDate));
        row.append($('<td data-column="등록일">').text(formattedCreateDate));

        if (userRole == '관리자' || id == data.memberId) {
            row.append($('<td style="display: flex; justify-content: center;" data-column="Actions">').append(deleteButton(data)));
        } else {
            row.append($('<td data-column="Actions">').text(""));
        }

        return row;
    }

    function updateProduct(productId,imageUrl,memo,no, category, location, price, currentPrice, minPrice, expectedPrice, saleDate, managerName, managerPhone, description, link,memberId) {
        const data = JSON.stringify({
            id:productId,
            images:[imageUrl],
            memo:memo,
            no:no,
            category:category,
            location:location,
            price:price,
            currentPrice:currentPrice,
            minPrice:minPrice,
            expectedPrice:expectedPrice,
            saleDate:saleDate,
            managerName:managerName,
            managerPhone:managerPhone,
            description:description,
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

    function addProduct(imageUrl,memo,no, category, location, price, currentPrice, minPrice, expectedPrice, saleDate, managerName, managerPhone, description,link) {
        const data = JSON.stringify({
            images:[imageUrl],
            memo:memo,
            no:no,
            category:category,
            location:location,
            price:price,
            currentPrice:currentPrice,
            minPrice:minPrice,
            expectedPrice:expectedPrice,
            saleDate:saleDate,
            managerName:managerName,
            managerPhone:managerPhone,
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


