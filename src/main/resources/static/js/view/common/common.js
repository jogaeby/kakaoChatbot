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

function createTableHeader(headers) {

    const thead = $('<thead style="background-color: #e9ecef;">');
    const headerRow = $('<tr>');

    headers.forEach(header => {
        headerRow.append($('<th>').text(header));
    });

    thead.append(headerRow);
    return thead;
}

function renderTable(dataList,headers,createTableRow) {
    const container = $(".datatable-container").empty();

    const table = $('<table>').addClass('datatable-table');
    const thead = createTableHeader(headers);
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