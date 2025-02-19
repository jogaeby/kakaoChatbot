const cardContainer = document.getElementById('cardContainer');
const loading = document.getElementById('loading');
let page = 0;          // 페이지 번호 (0부터 시작)
let isLoading = false; // 로딩 중 상태
let hasMore = true;    // 더 불러올 데이터 여부
let itemIndex = 1;     // 순번 (1부터 시작)

// 📍 URL에서 상품 ID 가져오기
const urlParams = new URLSearchParams(window.location.search);
const productId = urlParams.get('id');  // 예: ?id=123

// 🚩 카드 렌더링 함수 (특정 상품은 '선택 매물' 표시)
function renderCard(item, isSelected = false) {
    const card = document.createElement('div');
    card.className = 'card shadow-lg border-0 rounded-lg mt-3';
    card.setAttribute('data-product-id', item.productId ?? '');

    // 🏷️ 순번 또는 '선택 매물' 표시
    const label = isSelected ? '선택 매물' : `No: ${itemIndex++}`;

    card.innerHTML = `
 
        <img src="${item.images?.[0] ?? '/default-image.png'}" alt="Logo" class="card-img-top">
        <div class="card-header"><strong>${label}</strong></div>
        <div class="card-body">
            <div><strong>제목:</strong> ${item.title ?? ''}</div>
            <div><strong>타경번호:</strong> ${item.no ?? ''}</div>
            <div><strong>물건종류:</strong> ${item.category ?? ''}</div>
            <div><strong>소재지:</strong> ${item.location ?? ''}</div>
            <div><strong>감정가:</strong> ${item.price?.toLocaleString() ?? ''}원</div>
            <div><strong>최저가:</strong> ${item.minPrice?.toLocaleString() ?? ''}원</div>
            <div><strong>예상 낙찰가:</strong> ${item.expectedPrice?.toLocaleString() ?? ''}원</div>
            <div><strong>매각 기일:</strong> ${item.saleDate ?? ''}</div>
            <div><strong>담당자:</strong> ${item.managerName ?? ''}</div>
            <div><strong>담당자 연락처:</strong> ${item.managerPhone ?? ''}</div>
            <div><strong>링크:</strong> <a href="${item.link ?? '#'}" target="_blank">상세 보기</a></div>
            <div><strong>작성자:</strong> ${item.memberId ?? ''}</div>
            <div><strong>작성일:</strong> ${item.createDate ?? ''}</div>
        </div>
    `;

    // 🚀 선택 매물은 상단에, 일반 매물은 하단에 추가
    if (isSelected) {
        cardContainer.prepend(card);  // 선택 매물은 상단
    } else {
        cardContainer.appendChild(card);  // 일반 매물은 하단
    }

    return card;
}

// 🚩 특정 상품 ID로 조회 (선택 매물 표시)
async function fetchProductById(productId) {
    try {
        const response = await fetch(`/product/${productId}`);
        if (!response.ok) throw new Error('상품을 찾을 수 없습니다.');
        const product = await response.json();

        console.log(`🔑 선택 매물 조회 성공: ${product.productId}`);
        renderCard(product, true);  // 🚀 선택 매물로 렌더링
    } catch (error) {
        console.error('❌ 선택 매물 조회 실패:', error);
        alert('해당 상품을 찾을 수 없습니다.');
    }
}

// 🚩 일반 데이터 불러오기 (무한 스크롤)
async function fetchData() {
    if (isLoading || !hasMore) return;
    isLoading = true;
    loading.style.display = 'block';

    try {
        const response = await fetch(`/product/previous?page=${page}&size=10`);
        const data = await response.json();

        console.log('📦 일반 매물 데이터:', data);

        // 일반 매물 렌더링
        if (data.content && data.content.length > 0) {
            data.content.forEach(item => renderCard(item));
            page++;  // 다음 페이지로 이동
        }

        // 마지막 페이지 여부
        if (data.last) {
            hasMore = false;
            loading.innerText = '✅ 모든 데이터를 불러왔습니다.';
        }
    } catch (error) {
        console.error('❌ 데이터 불러오기 실패:', error);
    } finally {
        isLoading = false;
        loading.style.display = 'none';
    }
}

// 🚩 무한 스크롤 감지
window.addEventListener('scroll', () => {
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 10) {
        fetchData();
    }
});

// 🚩 실행 로직
if (productId) {
    console.log(`🔑 특정 상품 ID 감지됨: ${productId}`);
    fetchProductById(productId);  // 🚀 선택 매물로 상단에 표시
}

fetchData();  // 일반 매물 로딩
