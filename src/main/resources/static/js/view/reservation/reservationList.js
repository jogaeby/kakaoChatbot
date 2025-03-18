$(document).ready(function() {
    const headers = [
        'No', '수강생 이름', '수강생 연락처', '선생님 이름', '선생님 연락처', '수강생 정보','체험일시','접수일', '기타'
    ];
    const sortableColumns = ["매각 기일", "등록일"];

    const jwtToken = getCookie('session-id');
    const payload = parseJWT(jwtToken);
    const userRole = payload.role;
    const id = payload.id;

    getData()
    renderCategories()

    $(document).ready(function () {
        // 추가 모달: 이미지 입력 방식 변경 시, 해당 입력창 보이기/숨기기
        $('input[name="imageInputType"]').on('change', function () {
            if ($(this).val() === 'url') {
                $("#addProductImageUrl").val('');
                $("#addProductImageFile").val('');
                $('#imageUrlGroup').show();
                $('#imageFileGroup').hide();
            } else if ($(this).val() === 'file') {
                $("#addProductImageUrl").val('');
                $("#addProductImageFile").val('');
                $('#imageUrlGroup').hide();
                $('#imageFileGroup').show();
            }
        });

        // 업데이트 모달: 이미지 입력 방식 변경 시, 해당 입력창 보이기/숨기기
        $('input[name="updateImageInputType"]').on('change', function () {
            if ($(this).val() === 'url') {
                $("#updateProductImageUrl").val('');
                $("#updateProductImageFile").val('');
                $('#updateImageUrlGroup').show();
                $('#updateImageFileGroup').hide();
            } else if ($(this).val() === 'file') {
                $("#updateProductImageUrl").val('');
                $("#updateProductImageFile").val('');
                $('#updateImageUrlGroup').hide();
                $('#updateImageFileGroup').show();
            }
        });
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

    $("#addProductModalButton").on("click", function () {
        // name이 "imageInputType"인 input을 제외한 모든 input 초기화
        $('#addModal').find('input').not('[name="imageInputType"]').val('');
        $('#addModal').find('textarea').val('');

        const addDateInput = $('#addProductDisplayDate');

        addDateInput.attr('min', todayKST); // today를 최소값으로 설정
        addDateInput.val(todayKST); // 기본값으로 today 설정
        $("#addProductManagerName").val(payload.name)
        $("#addProductManagerPhone").val(payload.phone)
        $('#addModal').modal('show');
    });

    $("#addProductButton").on("click", function () {
        const imageUrl = $("#addProductImageUrl").val();
        const imageFile = $('#addProductImageFile')[0].files[0];
        const memo = $("#addProductMemo").val();
        const no = $("#addProductNo").val();
        const category = $("#addProductCategory").val();
        const location = $("#addProductLocation").val();
        const price = $("#addProductPrice").val().replace(/,/g, '');;
        const currentPrice = $("#addProductCurrentPrice").val().replace(/,/g, '');;
        const minPrice = $("#addProductMinPrice").val().replace(/,/g, '');;
        const expectedPrice = $("#addProductExpectedPrice").val().replace(/,/g, '');;
        const saleDate = $("#addProductSaleDate").val();
        const managerName = $("#addProductManagerName").val();
        const managerPhone = $("#addProductManagerPhone").val();
        const description = $("#addProductDescription").val();
        const link = $("#addProductLink").val();
        const displayDate = $("#addProductDisplayDate").val();

        if (!imageUrl && !imageFile) {
            alert("이미지를 입력하세요.")
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
        if (!managerPhone ) {
            alert("담당자 연락처를 입력하세요.")
            return
        }

        if (!validatePhoneNumber(managerPhone)) {
            alert("연락처 형식을 맞춰주세요. 예)01055557777")
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

        if (!displayDate) {
            alert("노출 날짜를 선택하세요.")
            return
        }

        addProduct(imageUrl, imageFile, memo, no, category, location, price, currentPrice, minPrice, expectedPrice, saleDate, managerName, managerPhone, description, link, displayDate)
    })

    $("#updateProductButton").on("click", function () {


        const productId = $('#updateProductId').val();
        const imageUrl = $("#updateProductImageUrl").val();
        const imageFile = $('#updateProductImageFile')[0].files[0];

        const memo = $('#updateProductMemo').val();
        const no = $('#updateProductNo').val();
        const category = $('#updateProductCategory').val();
        const location = $('#updateProductLocation').val();
        const price = $('#updateProductPrice').val().replace(/,/g, '');;
        const currentPrice = $('#updateProductCurrentPrice').val().replace(/,/g, '');;
        const minPrice = $('#updateProductMinPrice').val().replace(/,/g, '');;
        const expectedPrice = $('#updateProductExpectedPrice').val().replace(/,/g, '');;
        const saleDate = $('#updateProductSaleDate').val();
        const managerName = $('#updateProductManagerName').val();
        const managerPhone = $('#updateProductManagerPhone').val();
        const description = $('#updateProductDescription').val();
        const link = $('#updateProductLink').val();
        const memberId = $('#updateProductMemberId').val();
        const displayDate = $('#updateProductDisplayDate').val();

        if (!productId || !memberId) {
            alert("관리자에게 문의하세요");
            return;
        }

        if (!imageUrl && !imageFile) {
            alert("이미지를 입력하세요.")
            return
        }

        if (!memo) {
            alert("메모를 입력하세요.");
            return;
        }

        if (!no) {
            alert("타경번호를 입력하세요.");
            return;
        }

        if (!category) {
            alert("물건종류를 입력하세요.");
            return;
        }
        if (!location) {
            alert("소재지를 입력하세요.");
            return;
        }
        if (!price) {
            alert("감정가를 입력하세요.");
            return;
        }
        if (!currentPrice) {
            alert("현시세를 입력하세요.");
            return;
        }
        if (!minPrice) {
            alert("최저가를 입력하세요.");
            return;
        }
        if (!expectedPrice) {
            alert("예상 낙찰가를 입력하세요.");
            return;
        }

        if (!saleDate) {
            alert("매각 기일을 선택하세요.");
            return;
        }
        if (!managerName) {
            alert("담당자 이름을 입력하세요.");
            return;
        }
        if (!managerPhone) {
            alert("담당자 연락처를 입력하세요.");
            return;
        }

        if (!validatePhoneNumber(managerPhone)) {
            alert("연락처 형식을 맞춰주세요. 예)01055557777")
            return
        }

        if (!description) {
            alert("장단점을 입력하세요.");
            return;
        }
        if (!link) {
            alert("링크를 입력하세요.");
            return;
        }
        if (!displayDate) {
            alert("링크를 입력하세요.");
            return;
        }
        updateProduct(productId, imageUrl, imageFile, memo, no, category, location, price, currentPrice, minPrice, expectedPrice, saleDate, managerName, managerPhone, description, link, memberId, displayDate);
    });

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

        fetch(`/reservation/trial/list?page=${page}&size=${size}&sort=${sort}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                return response.json()
            }).then(data => {
            console.log(data)
            renderTable(data, headers, createTableRow, sortableColumns)
        })
            .catch(error => {
                console.log(error)
            });
    }

    function createTableRow(data, index) {
        const formattedCreateDate = formatDate(data.createDate);
        const formattedReservationDate = formatDate(data.reservationDate);
        const row = $(`<tr id = ${data.id}>`);
        row.append($('<td data-column="Index">').text(index + 1));

        const idCell = $('<td data-column="Memo">').text(data.studentName)
            .css({cursor: 'pointer', color: 'blue'});
        idCell.on('click', () => openUpdateModal(data));

        row.append(idCell);
        row.append($('<td data-column="Member ID">').text(data.studentPhone));
        row.append($('<td data-column="Status">').text(data.teacherName));
        row.append($('<td data-column="Status">').text(data.teacherPhone));
        row.append($('<td data-column="Status">').text(data.studentInfo));
        row.append($('<td data-column="Status">').text(formattedReservationDate));
        // 날짜 포맷 변경 (YYYY-MM-DD)

        row.append($('<td data-column="등록일">').text(formattedCreateDate));

        if (userRole == '관리자' || id == data.memberId) {
            row.append($('<td style="display: flex; justify-content: center;" data-column="Actions">').append(deleteButton(data)));
        } else {
            row.append($('<td data-column="Actions">').text(""));
        }
        return row;
    }

    function updateProduct(productId, imageUrl, imageFile, memo, no, category, location, price, currentPrice, minPrice, expectedPrice, saleDate, managerName, managerPhone, description, link, memberId, displayDate) {
        const formData = new FormData();

        formData.append('id', productId);

        // 이미지 파일이 선택되었으면 추가
        if (imageFile) {
            formData.append('imageFile', imageFile);
        }

        formData.append('images', imageUrl);
        formData.append('memo', memo);
        formData.append('no', no);
        formData.append('category', category);
        formData.append('location', location);
        formData.append('price', price);
        formData.append('currentPrice', currentPrice);
        formData.append('minPrice', minPrice);
        formData.append('expectedPrice', expectedPrice);
        formData.append('saleDate', saleDate);
        formData.append('managerName', managerName);
        formData.append('managerPhone', managerPhone);
        formData.append('description', description);
        formData.append('link', link);
        formData.append('memberId', memberId);
        formData.append('displayDate', displayDate);

        fetch(`/product`, {
            method: 'PATCH',
            body: formData  // 브라우저가 자동으로 multipart/form-data 설정
        })
            .then(response => {
                if (response.ok) {
                    alert("성공적으로 수정하였습니다.");
                    $('#updateModal').modal('hide');
                    getData();
                } else {
                    alert("수정을 실패하였습니다.");
                }
            })
            .catch(error => {
                console.log(error);
            });
    }

    function addProduct(imageUrl, imageFile, memo, no, category, location, price, currentPrice, minPrice, expectedPrice, saleDate, managerName, managerPhone, description, link, displayDate) {
        const formData = new FormData();
        // 이미지 파일이 선택된 경우에만 파일 데이터를 추가 (둘 중 하나만 선택하도록 설계된 경우)
        console.log(imageFile)

        if (imageFile) {
            formData.append('imageFile', imageFile);
        }
        formData.append('images', imageUrl);
        formData.append('memo', memo);
        formData.append('no', no);
        formData.append('category', category);
        formData.append('location', location);
        formData.append('price', price);
        formData.append('currentPrice', currentPrice);
        formData.append('minPrice', minPrice);
        formData.append('expectedPrice', expectedPrice);
        formData.append('saleDate', saleDate);
        formData.append('managerName', managerName);
        formData.append('managerPhone', managerPhone);
        formData.append('description', description);
        formData.append('link', link);
        formData.append('displayDate', displayDate);

        fetch(`/product`, {
            method: 'POST',
            body: formData  // Content-Type은 브라우저가 자동으로 multipart/form-data로 설정합니다.
        })
            .then(response => {
                if (response.ok) {
                    alert("성공적으로 추가하였습니다.");
                    $('#addModal').modal('hide');
                    getData();
                } else {
                    alert("추가를 실패하였습니다.");
                }
            })
            .catch(error => {
                console.log(error);
            });
    }

    function deleteButton(data) {
        return $('<button>').text('삭제').addClass('btn btn-danger mx-lg-1 btn-sm').on('click', function () {
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
                if (response.ok) {
                    alert("성공적으로 삭제하였습니다.")
                    getData()
                } else {
                    alert("삭제를 실패하였습니다.")
                }
            })
            .catch(error => {
                console.log(error)
            });
    }
})


