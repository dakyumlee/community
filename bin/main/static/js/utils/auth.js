const Auth = {
	getToken() {
		return localStorage.getItem(STORAGE_KEYS.TOKEN);
	},
	setToken(token) {
		localStorage.setItem(STORAGE_KEYS.TOKEN, token);
	},
	getUser() {
		const userData = localStorage.getItem(STORAGE_KEYS.USER);
		return userData ? JSON.parse(userData) : null;
	},
	setUser(user) {
		localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(user));
		this.updateHeaderUI();
	},
	isAuthenticated() {
		return !!this.getToken();
	},
	logout() {
		localStorage.removeItem(STORAGE_KEYS.TOKEN);
		localStorage.removeItem(STORAGE_KEYS.USER);
		this.updateHeaderUI();
		window.location.href = '/index';
	},
	redirectIfAuthenticated() {
		if (this.isAuthenticated()) {
			window.location.href = '/index';
			return true;
		}
		return false;
	},
	redirectIfNotAuthenticated() {
		if (!this.isAuthenticated()) {
			window.location.href = '/login';
			return true;
		}
		return false;
	},
	updateHeaderUI() {
		const user = this.getUser();
		const navGuest = document.getElementById('nav-guest');
		const navUser = document.getElementById('nav-user');
		const userNickname = document.getElementById('user-nickname');
		const adminLink = document.getElementById('admin-link');
		if (user && this.isAuthenticated()) {
			if (navGuest) navGuest.classList.add('hidden');
			if (navUser) navUser.classList.remove('hidden');
			if (userNickname) {
				userNickname.textContent = user.nickname;
			}
			if (adminLink && user.isAdmin) {
				adminLink.classList.remove('hidden');
			} else if (adminLink) {
				adminLink.classList.add('hidden');
			}
		} else {
			if (navGuest) navGuest.classList.remove('hidden');
			if (navUser) navUser.classList.add('hidden');
		}
	}
};
console.log('Auth loaded');