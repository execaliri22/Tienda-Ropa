const BASE_URL = '/api';
// const MOCK_USER_ID = '12345'; // Eliminado y reemplazado por la obtención dinámica del ID

// Variable global para mantener el estado del filtro de categoría
let currentCategoryId = null;

// Nueva función para obtener el ID del usuario
function getUserId() {
    const userData = JSON.parse(localStorage.getItem('user'));
    // El 'id' ahora se devuelve en AuthController.java y se almacena aquí.
    return userData ? userData.id : null; 
}


document.addEventListener('DOMContentLoaded', () => {
    // Lógica de inicio para cada página
    if (document.querySelector('#product-catalog')) {
        loadCategoriesAndFilters(); 
    } else if (document.querySelector('#cart-items')) {
        loadCart();
    } else if (document.querySelector('#profile-main')) { // profile.html
        loadProfileAndHistory();
    } else if (document.querySelector('#order-history-page')) { // Página de historial de pedidos
        loadOrderHistoryPage();
    } else if (document.getElementById('product-detail-info')) {
        setupProductDetailPage();
    } else if (document.querySelector('#wishlist-container')) {
        loadWishlist();
    }
    
    // Función de actualización de avatar en index2.html
    updateAvatarDisplay();
});

// NUEVA FUNCIÓN: Actualiza el avatar en la barra de navegación y panel lateral
function updateAvatarDisplay() {
    const userData = JSON.parse(localStorage.getItem('user'));
    // Usamos 'images/default_avatar.png' si no hay URL guardada.
    const avatarPath = userData ? (userData.profilePictureUrl || 'images/default_avatar.png') : 'images/default_avatar.png';
    const finalUrl = avatarPath; // Asume que la URL es relativa a static/

    if (document.getElementById('userAvatarNav')) {
        document.getElementById('userAvatarNav').src = finalUrl;
    }
    if (document.getElementById('profileAvatar')) {
        document.getElementById('profileAvatar').src = finalUrl;
    }
}


// =========================================================
// LÓGICA DE AUTENTICACIÓN
// =========================================================
async function handleLogin(event) {
    event.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch(`${BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        
        const result = await response.json();

        if (response.ok) {
            // Se realiza una solicitud adicional para obtener la URL de la foto y la dirección.
            const userResponse = await fetch(`${BASE_URL}/users/${result.id}`);
            const fullUserData = await userResponse.json();
            
            // Adjuntar los datos adicionales al objeto de sesión
            result.profilePictureUrl = fullUserData.profilePictureUrl || 'images/default_avatar.png';
            result.direccion = fullUserData.direccion || ''; 
            
            localStorage.setItem('user', JSON.stringify(result)); 
            alert(`Login exitoso para ${result.name}. Redirigiendo...`);
            window.location.href = 'index2.html'; 
        } else {
            alert(`Error de Login: ${result.message || 'Credenciales inválidas.'}`);
        }
    } catch (error) {
        console.error('Error en handleLogin:', error);
        alert('Ocurrió un error de conexión con el servidor.');
    }
}

function logout() {
    localStorage.removeItem('user');
    window.location.href = "login.html";
}

async function handleRegister(event) {
    event.preventDefault();
    const name = document.getElementById('reg-name').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;
    
    // El campo 'Direccion' es opcional en register.html, lo enviamos vacío.
    const userPayload = { 
        nombre: name, 
        email: email, 
        password: password, 
        direccion: '' 
    };

    try {
        const response = await fetch(`${BASE_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userPayload) 
        });
        
        // El controlador Java devuelve un String simple (ej. "Usuario registrado exitosamente")
        const resultText = await response.text(); 

        if (response.ok && resultText.includes('exitosamente')) {
            alert('Registro exitoso. Por favor, inicia sesión.');
            window.location.href = 'login.html'; 
        } else {
            // El backend devuelve el mensaje de error en el cuerpo de la respuesta.
            alert(`Error de Registro: ${resultText || 'Hubo un problema al registrar el usuario.'}`);
        }
    } catch (error) {
        console.error('Error en handleRegister:', error);
        alert('Ocurrió un error de conexión con el servidor.');
    }
}


// =========================================================
// LÓGICA DE FILTRADO Y BÚSQUEDA
// =========================================================

async function loadCategoriesAndFilters() {
    const categoriesContainer = document.getElementById('category-submenu'); 
    if (!categoriesContainer) return;
    
    categoriesContainer.innerHTML = ''; 
    
    const allButton = document.createElement('button');
    allButton.textContent = 'Todas';
    allButton.className = 'category-button active';
    allButton.onclick = () => filterProducts(null);
    categoriesContainer.appendChild(allButton);
    
    loadProducts(null, null); 

    try {
        const response = await fetch(`${BASE_URL}/categories`); 
        const categories = await response.json();

        categories.forEach(category => {
            const button = document.createElement('button');
            button.textContent = category.nombre;
            button.className = 'category-button';
            button.onclick = () => filterProducts(category.id);
            categoriesContainer.appendChild(button);
        });

    } catch (error) {
        console.error('Error al cargar categorías:', error);
    }
}

function filterProducts(categoryId) {
    currentCategoryId = categoryId;
    
    document.querySelectorAll('.category-button').forEach(btn => btn.classList.remove('active'));
    
    const categoriesContainer = document.getElementById('category-submenu');
    if (categoriesContainer) {
        const buttons = categoriesContainer.querySelectorAll('.category-button');
        buttons.forEach(btn => {
            if (categoryId === null && btn.textContent === 'Todas') {
                btn.classList.add('active');
            } else if (categoryId !== null && btn.onclick.toString().includes(`'${categoryId}'`)) {
                btn.classList.add('active');
            }
        });
    }
    
    const searchInput = document.getElementById('search-input');
    const searchTerm = searchInput ? searchInput.value : null;
    loadProducts(currentCategoryId, searchTerm);
}

function searchProducts() {
    const searchTerm = document.getElementById('search-input').value;
    loadProducts(currentCategoryId, searchTerm);
}


// =========================================================
// LÓGICA DE CATÁLOGO (products.html)
// =========================================================
async function loadProducts(categoryId = null, searchTerm = null) {
    const container = document.querySelector('#product-catalog');
    if (!container) return;
    
    let url = `${BASE_URL}/products`;
    const params = new URLSearchParams();

    if (categoryId) {
        params.append('categoryId', categoryId);
    }
    if (searchTerm) {
        params.append('search', searchTerm);
    }
    
    if (params.toString()) {
        url += '?' + params.toString(); 
    }

    try {
        const response = await fetch(url);
        const products = await response.json();

        container.innerHTML = '';

        if (products.length === 0) {
            container.innerHTML = `<p>No se encontraron productos para los filtros aplicados (Categoría: ${categoryId || 'Todas'}, Búsqueda: "${searchTerm || 'ninguna'}").</p>`;
            return;
        }

        products.forEach(product => {
            const productCard = createProductCard(product);
            container.appendChild(productCard);
        });

    } catch (error) {
        console.error('Error al cargar productos:', error);
        container.innerHTML = '<p style="color: red;">Error al cargar el catálogo desde la API.</p>';
    }
}

function createProductCard(product) {
    const card = document.createElement('div');
    card.className = 'product-card';
    card.style.cssText = `
        flex: 0 0 calc(33.333% - 20px); 
        max-width: calc(33.333% - 20px); 
        border: 1px solid #e0e0e0; 
        padding: 20px; 
        text-align: center; 
        border-radius: 8px; 
        box-shadow: 0 4px 8px rgba(0,0,0,0.05);
        transition: transform 0.3s;
    `;
    card.onmouseover = () => card.style.transform = 'translateY(-5px)';
    card.onmouseout = () => card.style.transform = 'translateY(0)';


    const price = product.precio ? `$${product.precio.toFixed(2)}` : 'Precio no disponible';
    const imageUrl = product.imageUrl || 'images/placeholder.jpg';
    
    const categoryName = (product.categoria && product.categoria.nombre) ? product.categoria.nombre : 'Sin Categoría';

    card.innerHTML = `
        <img src="${imageUrl}" alt="${product.nombre}" style="width: 100%; height: auto; max-height: 200px; object-fit: cover; margin-bottom: 15px; border-radius: 4px;">
        
        <p class="product-category">${categoryName}</p>
        
        <h3 style="font-size: 18px; margin-bottom: 5px;">${product.nombre}</h3>
        <p style="font-size: 1.2em; font-weight: 600; color: #111; margin-bottom: 10px;">${price}</p>
        <p style="color: ${product.stock > 0 ? 'green' : 'red'}; margin-bottom: 15px;">Stock: ${product.stock > 0 ? 'En stock' : 'Agotado'}</p>
        <a href="product-detail.html?id=${product.id}" class="btn-2" style="margin-right: 5px;">Ver Detalle</a>
        <button class="btn-1 add-to-cart-btn" data-product-id="${product.id}" data-quantity="1" style="margin-top: 10px; padding: 7px 12px; font-size: 14px;">Añadir</button>
    `;

    card.querySelector('.add-to-cart-btn').addEventListener('click', (e) => {
        const productId = e.target.getAttribute('data-product-id');
        addToCart(productId, 1);
    });

    return card;
}


// =========================================================
// LÓGICA DE DETALLE (product-detail.html)
// =========================================================
async function setupProductDetailPage() {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');
    const productDetailInfo = document.getElementById('product-detail-info'); 

    if (!productId) {
        if(productDetailInfo) productDetailInfo.innerHTML = '<div style="color: red; padding: 20px;"><h1>ERROR</h1><p>ID de producto faltante en la URL (Ej: ?id=...).</p></div>';
        return;
    }
    
    if (!productDetailInfo) return; 

    try {
        const response = await fetch(`${BASE_URL}/products/${productId}`);

        if (!response.ok) {
            productDetailInfo.innerHTML = `<div style="color: red; padding: 20px;"><h1>Error ${response.status}</h1><p>El producto con ID ${productId} no se encontró o el servidor falló. Asegúrate de que el ID es correcto.</p></div>`;
            console.error(`Error al buscar producto ${productId}. Estado: ${response.status}`);
            return;
        }

        const product = await response.json();

        document.getElementById('product-name').textContent = product.nombre;
        document.getElementById('product-price').textContent = `Precio: $${product.precio.toFixed(2)}`;
        document.getElementById('product-stock').textContent = `Stock Disponible: ${product.stock}`;
        document.getElementById('product-image').src = product.imageUrl || 'images/placeholder.jpg';
        document.getElementById('product-description').textContent = product.nombre + '. Descripción de calidad, materiales y tallas.';

        document.getElementById('add-to-cart').onclick = () => {
            const quantity = parseInt(document.getElementById('quantity').value);
            if (quantity > 0) {
                addToCart(product.id, quantity);
            }
        };

        // El corazón de Deseos
        document.getElementById('add-to-wishlist').onclick = () => {
            addToWishlist(product.id);
        };

    } catch (error) {
        productDetailInfo.innerHTML = '<div style="color: red; padding: 20px;"><h1>Error de Conexión</h1><p>No se pudo conectar con el servicio de productos. Verifique que Spring Boot esté activo.</p></div>';
        console.error('Error al cargar detalle del producto (Catch):', error);
    }
}


// =========================================================
// LÓGICA DE CARRITO Y CHECKOUT
// =========================================================
async function addToCart(productId, quantity) {
    const userId = getUserId(); // Obtener el ID del usuario
    if (!userId) { alert('Debe iniciar sesión para añadir al carrito.'); return; }

    try {
        // Usar el ID del usuario en la URL
        const url = `${BASE_URL}/users/${userId}/cart/add?productId=${productId}&quantity=${quantity}`; 
        const response = await fetch(url, { method: 'POST' });
        
        if (response.ok) {
            alert('¡Producto añadido al carrito!');
            if (document.querySelector('#cart-items')) {
                loadCart();
            }
        } else {
            alert('Error al añadir producto al carrito.');
        }
    } catch (error) {
        console.error('Error en addToCart:', error);
        alert('Ocurrió un error de conexión al añadir al carrito.');
    }
}

async function loadCart() {
    const cartItemsBody = document.querySelector('#cart-items');
    const cartTotalSpan = document.querySelector('#cart-total');
    const checkoutTotal = document.getElementById('checkout-total');
    
    if (!cartItemsBody || !cartTotalSpan) return;

    const userId = getUserId(); // Obtener el ID del usuario
    if (!userId) { 
        cartItemsBody.innerHTML = '<tr><td colspan="5" style="padding: 20px; text-align: center;">Por favor, inicie sesión para ver su carrito.</td></tr>'; 
        cartTotalSpan.textContent = `$0.00`;
        if (checkoutTotal) checkoutTotal.textContent = `$0.00`;
        return; 
    }

    try {
        // Usar el ID del usuario en la URL
        const response = await fetch(`${BASE_URL}/users/${userId}/cart`);
        const cart = await response.json();

        cartItemsBody.innerHTML = '';
        let total = 0;

        if (cart && cart.items && cart.items.length > 0) {
            cart.items.forEach(item => {
                total += item.subtotal;
                const row = document.createElement('tr');
                row.style.borderBottom = '1px solid #ddd';
                
                row.innerHTML = `
                    <td style="padding: 15px;">Producto ID: ${item.productId}</td>
                    <td style="padding: 15px;">
                        <input type="number" value="${item.cantidad}" min="1" 
                               style="width: 50px; padding: 5px; border-radius: 3px;" 
                               data-product-id="${item.productId}"
                               onchange="updateCartItemQuantity(this)">
                    </td>
                    <td style="padding: 15px;">$${(item.subtotal / item.cantidad).toFixed(2)}</td>
                    <td style="padding: 15px;">$${item.subtotal.toFixed(2)}</td>
                    <td style="padding: 15px;">
                        <button onclick="removeItemFromCart('${item.productId}')"
                                style="background: none; border: 1px solid red; color: red; padding: 5px 10px; border-radius: 4px; cursor: pointer;">Eliminar</button>
                    </td>
                `;
                cartItemsBody.appendChild(row);
            });
        } else {
            cartItemsBody.innerHTML = '<tr><td colspan="5" style="padding: 20px; text-align: center;">El carrito está vacío.</td></tr>';
        }

        cartTotalSpan.textContent = `$${total.toFixed(2)}`;
        if (checkoutTotal) {
            checkoutTotal.textContent = `$${total.toFixed(2)}`;
        }
        
    } catch (error) {
        console.error('Error al cargar el carrito:', error);
        cartItemsBody.innerHTML = '<tr><td colspan="5" style="padding: 20px; text-align: center; color: red;">Error al cargar el carrito.</td></tr>';
    }
}

async function updateCartItemQuantity(inputElement) {
    const userId = getUserId(); // Obtener el ID del usuario
    if (!userId) { alert('Debe iniciar sesión para actualizar el carrito.'); loadCart(); return; }

    const productId = inputElement.getAttribute('data-product-id');
    const newQuantity = parseInt(inputElement.value);

    if (newQuantity <= 0 || isNaN(newQuantity)) {
        alert("La cantidad debe ser mayor a cero.");
        loadCart(); 
        return;
    }

    try {
        // Usar el ID del usuario en la URL
        const url = `${BASE_URL}/users/${userId}/cart/update?productId=${productId}&quantity=${newQuantity}`; 
        const response = await fetch(url, { method: 'POST' });

        if (response.ok) {
            loadCart();
        } else {
            alert('Error al actualizar la cantidad en el servidor.');
            loadCart(); 
        }

    } catch (error) {
        console.error('Error en updateCartItemQuantity:', error);
    }
}

async function removeItemFromCart(productId) {
    const userId = getUserId(); // Obtener el ID del usuario
    if (!userId) { alert('Debe iniciar sesión para modificar el carrito.'); return; }

    try {
        // Usar el ID del usuario en la URL
        const url = `${BASE_URL}/users/${userId}/cart/remove?productId=${productId}`; 
        const response = await fetch(url, { method: 'POST' }); 
        
        if (response.ok) {
            alert('Producto eliminado del carrito.');
            loadCart();
        } else {
            alert('Error al eliminar producto del carrito.');
        }
    } catch (error) {
        console.error('Error en removeItemFromCart:', error);
    }
}

async function checkout(event) {
    event.preventDefault();
    const userId = getUserId(); // Obtener el ID del usuario
    if (!userId) { alert('Debe iniciar sesión para finalizar la compra.'); return; }
    
    try {
        // Usar el ID del usuario en la URL
        const response = await fetch(`${BASE_URL}/users/${userId}/cart/checkout`, { method: 'POST' });
        
        if (response.ok) {
            const order = await response.json();
            alert(`¡Compra realizada exitosamente! ID de Pedido: ${order.id}. Total: $${order.total.toFixed(2)}`);
            window.location.href = 'profile.html';
        } else {
            alert('Error al finalizar la compra. Verifique que su carrito no esté vacío.');
        }
    } catch (error) {
        console.error('Error en checkout:', error);
        alert('Ocurrió un error al intentar finalizar la compra.');
    }
}


// =========================================================
// LÓGICA DE LISTA DE DESEOS
// =========================================================
async function addToWishlist(productId) {
    const userId = getUserId(); // Obtener el ID del usuario
    if (!userId) { alert('Debe iniciar sesión para añadir a la lista de deseos.'); return; }

    try {
        // Usar el ID del usuario en la URL
        const url = `${BASE_URL}/users/${userId}/wishlist/add?productId=${productId}`;
        const response = await fetch(url, { method: 'POST' });
        
        if (response.ok) {
            alert('¡Producto añadido a la Lista de Deseos!');
            if (document.querySelector('#wishlist-container')) {
                loadWishlist();
            }
        } else {
            alert('Error al añadir producto a la Lista de Deseos.');
        }
    } catch (error) {
        console.error('Error en addToWishlist:', error);
    }
}

async function loadWishlist() {
    const container = document.querySelector('#wishlist-container');
    if (!container) return;
    
    const userId = getUserId(); // Obtener el ID del usuario
    if (!userId) { 
        container.innerHTML = '<p>Por favor, inicie sesión para ver su lista de deseos.</p>'; 
        return; 
    }

    try {
        // Usar el ID del usuario en la URL
        const response = await fetch(`${BASE_URL}/users/${userId}/wishlist`);
        const wishlist = await response.json();

        container.innerHTML = '';
        
        if (wishlist && wishlist.productos && wishlist.productos.length > 0) {
            wishlist.productos.forEach(product => {
                const card = createWishlistItemCard(product);
                container.appendChild(card);
            });
        } else {
            container.innerHTML = '<p>Tu lista de deseos está vacía.</p>';
        }
    } catch (error) {
        console.error('Error al cargar la Lista de Deseos:', error);
        container.innerHTML = '<p style="color: red;">Error al cargar la Lista de Deseos.</p>';
    }
}

function createWishlistItemCard(product) {
    const card = document.createElement('div');
    card.className = 'product-card';
    card.style.cssText = `
        flex: 0 0 calc(33.333% - 20px); 
        max-width: calc(33.333% - 20px); 
        border: 2px solid #f9d8d8;
        padding: 20px; 
        text-align: center; 
        border-radius: 8px; 
        box-shadow: 0 4px 8px rgba(255, 77, 77, 0.1); 
        transition: transform 0.3s;
        background-color: #fff;
    `;

    const imageUrl = product.imageUrl || 'images/placeholder.jpg';
    
    // HTML de la tarjeta simplificado a solo Ver Detalle y Eliminar
    card.innerHTML = `
        <img src="${imageUrl}" alt="${product.nombre}" style="width: 100%; height: auto; max-height: 200px; object-fit: cover; margin-bottom: 15px; border-radius: 4px;">
        <h3 style="font-size: 18px; margin-bottom: 5px;">${product.nombre}</h3>
        <p style="font-size: 1.2em; font-weight: 600; color: #111; margin-bottom: 15px;">Precio: $${product.precio ? product.precio.toFixed(2) : 'N/A'}</p>
        
        <div style="margin-top: 15px;">
            <a href="product-detail.html?id=${product.id}" class="btn-view-detail">Ver Detalle</a>
            <button class="btn-delete-wishlist" onclick="removeFromWishlist('${product.id}')">Eliminar</button>
        </div>
    `;
    return card;
}

async function removeFromWishlist(productId) {
    const userId = getUserId(); // Obtener el ID del usuario
    if (!userId) { alert('Debe iniciar sesión para eliminar de la lista de deseos.'); return; }
    
    try {
        // Usar el ID del usuario en la URL
        const url = `${BASE_URL}/users/${userId}/wishlist/remove?productId=${productId}`;
        const response = await fetch(url, { method: 'DELETE' }); 
        
        if (response.ok) {
            alert('Producto eliminado de la Lista de Deseos.');
            loadWishlist();
        } else {
            alert('Error al eliminar producto de la Lista de Deseos.');
        }
    } catch (error) {
        console.error('Error en removeFromWishlist:', error);
    }
}


// =========================================================
// LÓGICA DE PERFIL Y ÓRDENES (profile.html & order_history.html)
// =========================================================

/**
 * Llena el formulario de edición con los datos actuales del usuario desde el localStorage.
 */
function fillProfileForm(userData) {
    const nameInput = document.getElementById('update-name');
    const addressInput = document.getElementById('update-address');
    
    // Rellenar campos si existen
    if (nameInput) nameInput.value = userData.name || '';
    if (addressInput) addressInput.value = userData.direccion || '';
    
    // Configurar la imagen del avatar en el sidebar
    const avatarImg = document.getElementById('current-avatar');
    if (avatarImg) {
        const avatarPath = userData.profilePictureUrl || 'images/default_avatar.png';
        // La URL se construye relativa a /static/
        avatarImg.src = avatarPath;
    }
}

// Mantiene la carga de info y precarga del formulario de detalles
async function loadProfileAndHistory() {
    const profileInfo = document.querySelector('#profile-info');
    
    const userId = getUserId();
    const userData = JSON.parse(localStorage.getItem('user'));
    
    if (!profileInfo || !userId || !userData) {
        window.location.href = "login.html";
        return;
    }
    
    // 1. Mostrar información estática
    profileInfo.innerHTML = `
        <h3 class="oswald-title">Información Personal</h3>
        <p><strong>Nombre:</strong> ${userData.name || 'N/A'}</p>
        <p><strong>Email:</strong> ${userData.email || 'N/A'}</p>
        <p><strong>Dirección:</strong> ${userData.direccion || 'No especificada'}</p>
    `;

    // 2. Pre-llenar el formulario de edición de detalles y foto
    fillProfileForm(userData); 
}

// Carga el historial de pedidos en la página separada
async function loadOrderHistoryPage() {
    const orderHistoryBody = document.querySelector('#order-history-items');
    
    const userId = getUserId();
    if (!orderHistoryBody || !userId) {
        if(orderHistoryBody) orderHistoryBody.innerHTML = '<tr><td colspan="4" style="padding: 20px; text-align: center;">Debe iniciar sesión para ver su historial.</td></tr>';
        return;
    }
    
    try {
        // Usar el ID del usuario en la URL
        const response = await fetch(`${BASE_URL}/users/${userId}/orders`);
        const orders = await response.json();

        orderHistoryBody.innerHTML = '';
        
        if (orders && orders.length > 0) {
            orders.forEach(order => {
                const row = document.createElement('tr');
                row.style.borderBottom = '1px solid #ddd';
                row.innerHTML = `
                    <td style="padding: 15px;">${order.id.substring(0, 8)}...</td>
                    <td style="padding: 15px;">${new Date(order.fecha).toLocaleDateString()}</td>
                    <td style="padding: 15px;">$${order.total.toFixed(2)}</td>
                    <td style="padding: 15px; color: ${order.estado === 'PENDIENTE' ? 'orange' : 'green'}; font-weight: 600;">${order.estado}</td>
                `;
                orderHistoryBody.appendChild(row);
            });
        } else {
            orderHistoryBody.innerHTML = '<tr><td colspan="4" style="padding: 20px; text-align: center;">Aún no tienes pedidos.</td></tr>';
        }
    } catch (error) {
        console.error('Error al cargar historial de pedidos:', error);
        orderHistoryBody.innerHTML = '<tr><td colspan="4" style="padding: 20px; text-align: center; color: red;">Error al cargar el historial.</td></tr>';
    }
}

/**
 * Maneja el envío del formulario de actualización de datos personales (Nombre y Dirección).
 */
async function handleProfileDetailsUpdate(event) {
    event.preventDefault();
    
    const userId = getUserId();
    if (!userId) { alert('Sesión expirada. Por favor, inicie sesión.'); return; }

    const newName = document.getElementById('update-name').value;
    const newAddress = document.getElementById('update-address').value;

    const updatePayload = {
        nombre: newName,
        direccion: newAddress
    };

    try {
        // Llamada al endpoint PUT /api/users/{id}
        const response = await fetch(`${BASE_URL}/users/${userId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updatePayload)
        });

        if (response.ok) {
            const updatedUserData = await response.json();

            // 1. Actualizar localStorage con los nuevos datos
            const storedData = JSON.parse(localStorage.getItem('user'));
            storedData.name = updatedUserData.nombre;
            storedData.direccion = updatedUserData.direccion;
            
            localStorage.setItem('user', JSON.stringify(storedData));

            alert('✅ Datos personales actualizados exitosamente.');
            
            // 2. Recargar la sección de información para reflejar los cambios
            loadProfileAndHistory();
            
        } else if (response.status === 404) {
            alert('Error: Usuario no encontrado.');
        } else {
            const errorText = await response.text();
            alert(`Error al actualizar el perfil: ${errorText}`);
        }
    } catch (error) {
        console.error('Error en handleProfileDetailsUpdate:', error);
        alert('Ocurrió un error de conexión al actualizar los datos personales.');
    }
}

/**
 * Maneja el envío del formulario de cambio de contraseña.
 */
async function handlePasswordUpdate(event) {
    event.preventDefault();
    
    const userId = getUserId();
    if (!userId) { alert('Sesión expirada. Por favor, inicie sesión.'); return; }

    const newPassword = document.getElementById('new-password').value;
    const repeatPassword = document.getElementById('repeat-password').value;

    if (!newPassword || !repeatPassword) {
        alert('Ambos campos de contraseña son obligatorios.');
        return;
    }

    if (newPassword !== repeatPassword) {
        alert('❌ Error: Las contraseñas no coinciden. Inténtalo de nuevo.');
        return;
    }
    
    const updatePayload = {
        password: newPassword
    };

    try {
        // Llamada al endpoint PUT /api/users/{id}
        const response = await fetch(`${BASE_URL}/users/${userId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updatePayload)
        });

        if (response.ok) {
            // No necesitamos procesar los datos de usuario devueltos
            alert('✅ Contraseña cambiada exitosamente. Por favor, inicie sesión de nuevo para confirmar.');
            
            // Limpiar los campos de contraseña después del éxito
            document.getElementById('new-password').value = '';
            document.getElementById('repeat-password').value = '';
            
        } else if (response.status === 404) {
            alert('Error: Usuario no encontrado.');
        } else {
            const errorText = await response.text();
            alert(`Error al cambiar la contraseña: ${errorText}`);
        }
    } catch (error) {
        console.error('Error en handlePasswordUpdate:', error);
        alert('Ocurrió un error de conexión al cambiar la contraseña.');
    }
}


/**
 * Maneja la subida de la foto de perfil.
 */
async function handleProfilePictureUpload(event) {
    event.preventDefault();
    
    const userId = getUserId();
    const fileInput = document.getElementById('file-input');
    
    if (!userId) { alert('Sesión expirada. Por favor, inicie sesión.'); return; }
    if (fileInput.files.length === 0) { return; } 

    // Usar FormData para enviar el archivo
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    try {
        // Llamada al endpoint POST /api/users/{userId}/profile-picture
        const response = await fetch(`${BASE_URL}/users/${userId}/profile-picture`, {
            method: 'POST',
            body: formData 
        });

        if (response.ok) {
            const newPath = await response.text(); 
            
            // 1. Actualizar localStorage con la nueva ruta
            const storedData = JSON.parse(localStorage.getItem('user'));
            storedData.profilePictureUrl = newPath;
            localStorage.setItem('user', JSON.stringify(storedData));

            alert('✅ Foto de perfil subida exitosamente.');
            
            // 2. Recargar las imágenes en la página y en el index2.html
            fillProfileForm(storedData);
            updateAvatarDisplay(); // Actualiza el avatar en index2.html/panel lateral

        } else {
            const errorText = await response.text();
            alert(`Error al subir la foto: ${errorText}`);
        }
    } catch (error) {
        console.error('Error en handleProfilePictureUpload:', error);
        alert('Ocurrió un error de conexión al subir la foto.');
    }
}

/**
 * Maneja la eliminación de la foto de perfil.
 */
async function handleProfilePictureDelete() {
    const userId = getUserId();
    if (!userId) { alert('Sesión expirada. Por favor, inicie sesión.'); return; }

    if (!confirm('¿Estás seguro de que deseas eliminar tu foto de perfil? Se restablecerá a la imagen por defecto.')) {
        return;
    }

    try {
        // Llamada al endpoint DELETE /api/users/{userId}/profile-picture
        const response = await fetch(`${BASE_URL}/users/${userId}/profile-picture`, {
            method: 'DELETE'
        });

        if (response.ok) {
            const defaultPath = await response.text(); 
            
            // 1. Actualizar localStorage con la ruta por defecto
            const storedData = JSON.parse(localStorage.getItem('user'));
            storedData.profilePictureUrl = defaultPath;
            localStorage.setItem('user', JSON.stringify(storedData));

            alert('✅ Foto de perfil eliminada exitosamente.');
            
            // 2. Recargar las imágenes en la página y en el index2.html
            fillProfileForm(storedData);
            updateAvatarDisplay();

        } else {
            const errorText = await response.text();
            alert(`Error al eliminar la foto: ${errorText}`);
        }
    } catch (error) {
        console.error('Error en handleProfilePictureDelete:', error);
        alert('Ocurrió un error de conexión al eliminar la foto.');
    }
}


// =========================================================
// EXPOSICIÓN DE FUNCIONES GLOBALES
// =========================================================
window.handleLogin = handleLogin;
window.handleRegister = handleRegister; 
window.checkout = checkout;
window.removeItemFromCart = removeItemFromCart;
window.addToWishlist = addToWishlist;
window.removeFromWishlist = removeFromWishlist;
window.addToCart = addToCart;
window.logout = logout; 
window.updateCartItemQuantity = updateCartItemQuantity; 
window.loadCategoriesAndFilters = loadCategoriesAndFilters; 
window.filterProducts = filterProducts;
window.searchProducts = searchProducts;
window.handleProfileDetailsUpdate = handleProfileDetailsUpdate; 
window.handlePasswordUpdate = handlePasswordUpdate;           
window.loadOrderHistoryPage = loadOrderHistoryPage;
window.handleProfilePictureUpload = handleProfilePictureUpload;
window.handleProfilePictureDelete = handleProfilePictureDelete;
window.updateAvatarDisplay = updateAvatarDisplay; // Expuesta para uso en index2.html