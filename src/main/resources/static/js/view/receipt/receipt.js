function executeAssign() {
    if (confirm("해당 접수내역을 배정하시겠습니까?")) {
        $('#loadingOverlay').show();

        const id = $("#receiptId").text();
        const sheetName = $("#sheetName").text();
        const managerPhone = $("#managerPhone").text();
        const managerName = $("#managerName").text();
        const customerName = $("#customerName").text();
        const customerPhone = $("#customerPhone").text();
        const address = $("#address").text();
        const symptom = $("#symptom").text();
        // const inquiries = $("#inquiries").text();

        fetch('/receipt/assign', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                id:id,
                sheetName:sheetName,
                managerName:managerName,
                managerPhone:managerPhone,
                customerName:customerName,
                customerPhone:customerPhone,
                address:address,
                symptom:symptom,
                // inquiries:inquiries,
            })
        })
            .then(response => {
                if (!response.ok) {
                    alert("❌배정을 실패하였습니다.❌")
                    throw new Error("서버 오류");
                }else {
                    alert("✅성공적으로 배정이 완료하였습니다.✅")
                    location.reload()
                }
            }).catch(error => {
            console.error("에러:", error);
        }).finally(() => $('#loadingOverlay').hide());
    }
}

function executeComplete() {
    if (confirm("해당 접수내역을 AS완료처리 하시겠습니까?")) {
        $('#loadingOverlay').show();

        const id = $("#receiptId").text();
        const sheetName = $("#sheetName").text();
        const managerPhone = $("#managerPhone").text();
        const managerName = $("#managerName").text();
        const customerName = $("#customerName").text();
        const customerPhone = $("#customerPhone").text();
        const address = $("#address").text();
        const symptom = $("#symptom").text();
        // const inquiries = $("#inquiries").text();

        fetch('/receipt/complete', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                id:id,
                sheetName:sheetName,
                managerName:managerName,
                managerPhone:managerPhone,
                customerName:customerName,
                customerPhone:customerPhone,
                address:address,
                symptom:symptom,
                // inquiries:inquiries,
            })
        })
            .then(response => {
                if (!response.ok) {
                    alert("❌AS완료처리를 실패하였습니다.❌")
                    throw new Error("서버 오류");
                }else {
                    alert("✅성공적으로 AS완료처리 되었습니다.✅")
                    location.reload()
                }
            }).catch(error => {
            console.error("에러:", error);
        }).finally(() => $('#loadingOverlay').hide());
    }
}
