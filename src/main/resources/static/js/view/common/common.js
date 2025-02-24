const categories = [
    { name: '메모', value: 'title' },
    { name: '타경번호', value: 'no' },
    { name: '물건종류', value: 'category' },
    { name: '소재지', value: 'location' },
    { name: '매각기일', value: 'saleDate' },
    { name: '등록일', value: 'createDate' }
];
const realEstateCategories = [
    { name: '타경번호', value: 'no' },
    { name: '물건종류', value: 'category' },
    { name: '소재지', value: 'location' },
    { name: '매각기일', value: 'saleDate' },
    { name: '등록일', value: 'createDate' }
];

const todayKST = (() => {
    const now = new Date();
    // (9*60 + now.getTimezoneOffset())는 두 시간대 간의 차이를 분 단위로 계산합니다.
    const koreaTime = new Date(now.getTime() + (9 * 60 + now.getTimezoneOffset()) * 60000);
    const year = koreaTime.getFullYear();
    const month = String(koreaTime.getMonth() + 1).padStart(2, '0');
    const day = String(koreaTime.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
})();

function validatePhoneNumber(phoneNumber) {
    const regex = /^010\d{8}$/;
    return regex.test(phoneNumber);
}

$(document).ready(function() {
    // 숫자 입력 및 천 단위 콤마 처리 함수
    function applyNumberInputFormat(className) {
        $(`.${className}`).on('input', function() {
            let value = $(this).val().replace(/[^0-9]/g, ''); // 숫자만 허용
            $(this).val(value.replace(/\B(?=(\d{3})+(?!\d))/g, ',')); // 천 단위 콤마
        });

        // 포커스 해제 시 최종 포맷
        $(`.${className}`).on('blur', function() {
            let value = $(this).val().replace(/[^0-9]/g, '');
            $(this).val(value ? parseInt(value, 10).toLocaleString() : '');
        });
    }

    // 함수 호출
    applyNumberInputFormat('number-input');
});

function formatNumberWithComma(number) {
    if (typeof number !== 'number' && typeof number !== 'string') {
        return '';
    }

    // 숫자로 변환하고, 천 단위 콤마 적용
    const num = parseFloat(number.toString().replace(/[^0-9]/g, ''));

    return isNaN(num) ? '' : num.toLocaleString();
}



function renderCategoriesToRealEstate() {
    const selectElement = document.getElementById('categorySelect');

    // 기존 옵션 제거 (기본 선택 옵션 제외)
    selectElement.innerHTML = '<option value="">카테고리 선택</option>';

    // 카테고리 배열을 순회하며 옵션 추가
    realEstateCategories.forEach(category => {
        const option = document.createElement('option');
        option.value = category.value;
        option.textContent = category.name;
        selectElement.appendChild(option);
    });
}

function renderCategories() {
    const selectElement = document.getElementById('categorySelect');

    // 기존 옵션 제거 (기본 선택 옵션 제외)
    selectElement.innerHTML = '<option value="">카테고리 선택</option>';

    // 카테고리 배열을 순회하며 옵션 추가
    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category.value;
        option.textContent = category.name;
        selectElement.appendChild(option);
    });
}

function getCookie(name) {
    let cookieArr = document.cookie.split(";");
    for (let i = 0; i < cookieArr.length; i++) {
        let cookiePair = cookieArr[i].split("=");
        if (name === cookiePair[0].trim()) {
            return decodeURIComponent(cookiePair[1]);
        }
    }
    return null;
}

function parseJWT(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error('Invalid JWT token', e);
        return null;
    }
}

function createTableHeader(headers, sortableColumns  = []) {
    const thead = $('<thead style="background-color: #e9ecef;">');
    const headerRow = $('<tr>');

    headers.forEach(header => {
        const th = $('<th>').text(header);

        // 정렬 가능한 컬럼이면 버튼 추가
        if (sortableColumns.includes(header)) {
            const sortButton = $('<button class="sort-btn btn btn-sm btn-light ms-2">▲</button>')
                .attr('data-column', header)
                .attr('data-order', 'asc') // 기본 정렬: 오름차순
                .on('click', function () {
                    const column = $(this).attr('data-column');
                    let order = $(this).attr('data-order');
                    // 정렬 실행
                    sortTableByColumn(column, order);

                    // 정렬 방향 토글
                    if (order === 'asc') {
                        $(this).attr('data-order', 'desc').text('▼');
                    } else {
                        $(this).attr('data-order', 'asc').text('▲');
                    }
                });

            th.append(sortButton);
        }

        headerRow.append(th);
    });

    thead.append(headerRow);
    return thead;
}
function sortTableByColumn(column, order) {
    const table = $('#dataTable'); // 테이블 ID
    const tbody = table.find('tbody');
    const rows = tbody.find('tr').get();

    // 행 정렬
    rows.sort((a, b) => {
        const cellA = $(a).find(`td[data-column="${column}"]`).text().trim();
        const cellB = $(b).find(`td[data-column="${column}"]`).text().trim();

        // 날짜 형식인지 확인
        const dateA = Date.parse(cellA);
        const dateB = Date.parse(cellB);

        if (!isNaN(dateA) && !isNaN(dateB)) {
            return order === 'asc' ? dateA - dateB : dateB - dateA;
        }

        // 일반 문자열 정렬
        return order === 'asc' ? cellA.localeCompare(cellB) : cellB.localeCompare(cellA);
    });

    // 정렬된 행을 다시 추가
    tbody.empty().append(rows);

    // 순번 재정렬 (1부터 다시)
    tbody.find('tr').each((index, row) => {
        $(row).find('td[data-column="Index"]').text(index + 1);
    });
    // ✅ 이벤트 위임 방식으로 모달 이벤트 유지
    $('#dataTable tbody').off('click', 'td[data-column="Memo"]');
    $('#dataTable tbody').on('click', 'td[data-column="Memo"]', function () {
        const productId = $(this).closest('tr').attr('id');

        getProduct(productId).then(product => {
            if (product) {
                openUpdateModal(product); // 모달 열기
            } else {
                console.log('상품 정보를 불러올 수 없습니다.');
            }
        });
    });
}
function getProduct(productId) {
    return fetch(`/product/${productId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .catch(error => {
            console.error('Error fetching product:', error);
            return null;
        });
}

function openUpdateModal(data) {
    const displayDate = data.displayDate;

    // 디스플레이 날짜가 오늘 또는 이전이거나 상태가 "이전매물"이면 노출 날짜 input을 readonly로 설정
    if ((displayDate && displayDate <= todayKST) || data.status === "이전매물") {
        $('#updateProductDisplayDate').attr('readonly', true);
    } else {
        $('#updateProductDisplayDate').removeAttr('readonly');
        $('#updateProductDisplayDate').attr('min', todayKST);
    }

    // 기존 이미지 데이터에 따라 이미지 입력 방식 결정
    if (data.images && (Array.isArray(data.images) ? data.images.length > 0 : data.images !== '')) {
        // data.images가 배열인 경우 첫 번째 값을 사용하고, 문자열인 경우 그대로 사용
        const imageUrl = Array.isArray(data.images) ? data.images[0] : data.images;
        $("input[name='updateImageInputType'][value='url']").prop('checked', true);
        $('#updateImageUrlGroup').show();
        $('#updateImageFileGroup').hide();
        $("#updateProductImageUrl").val(imageUrl);
    } else {
        // 기존 이미지가 없으면 파일 입력 방식을 기본으로 선택
        $("input[name='updateImageInputType'][value='file']").prop('checked', true);
        $('#updateImageUrlGroup').hide();
        $('#updateImageFileGroup').show();
        $("#updateProductImageFile").val('');
    }

    // 나머지 데이터 채우기
    $('#updateProductId').val(data.id);
    $('#updateProductMemo').val(data.memo);
    $('#updateProductNo').val(data.no);
    $('#updateProductCategory').val(data.category);
    $('#updateProductLocation').val(data.location);
    $('#updateProductPrice').val(formatNumberWithComma(data.price));
    $('#updateProductCurrentPrice').val(formatNumberWithComma(data.currentPrice));
    $('#updateProductMinPrice').val(formatNumberWithComma(data.minPrice));
    $('#updateProductExpectedPrice').val(formatNumberWithComma(data.expectedPrice));
    $('#updateProductSaleDate').val(data.saleDate);
    $('#updateProductManagerName').val(data.managerName);
    $('#updateProductManagerPhone').val(data.managerPhone);
    $('#updateProductDescription').val(data.description);
    $('#updateProductLink').val(data.link);
    $('#updateProductMemberId').val(data.memberId);
    $('#updateProductDisplayDate').val(displayDate);

    // 업데이트 모달 열기
    $('#updateModal').modal('show');
}

function renderTable(dataList,headers,createTableRow,sortableColumns) {
    const container = $(".datatable-container").empty();

    const table = $('<table>').addClass('datatable-table').attr('id','dataTable');
    const thead = createTableHeader(headers,sortableColumns);
    const tbody = $('<tbody>').addClass("orderTable");

    // 데이터가 없을 경우 "조회 데이터가 없습니다" 메시지를 표시
    if (dataList.length === 0) {
        const emptyRow = $('<tr>').append(
            $('<td>')
                .attr('colspan', headers.length) // 헤더 길이만큼 colspan 설정
                .css('text-align', 'center')    // 가운데 정렬
                .text('조회 데이터가 없습니다.') // 메시지 표시
        );
        tbody.append(emptyRow);
    } else {
        // 데이터가 있을 경우 테이블에 행 추가
        dataList.forEach((data, index) => {
            const row = createTableRow(data, index);
            tbody.append(row);
        });
    }
    table.append(thead).append(tbody);
    container.append(table);
}

// YYYY-MM-DD 형식으로 변환 (LocalDate)
function formatDate(dateString) {
    if (!dateString) return "";
    const date = new Date(dateString);
    return date.toISOString().split("T")[0]; // YYYY-MM-DD 형태로 반환
}

// YYYY-MM-DD HH:mm:ss 형식으로 변환 (LocalDateTime)
function formatDateTime(dateTimeString) {
    if (!dateTimeString) return "";
    const date = new Date(dateTimeString);
    return date.getFullYear() + "-" +
        String(date.getMonth() + 1).padStart(2, '0') + "-" +
        String(date.getDate()).padStart(2, '0') + " " +
        String(date.getHours()).padStart(2, '0') + ":" +
        String(date.getMinutes()).padStart(2, '0') + ":" +
        String(date.getSeconds()).padStart(2, '0');
}