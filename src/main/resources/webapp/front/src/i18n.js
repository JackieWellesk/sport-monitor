import { createI18n } from 'vue-i18n';
import zh from './locales/zh'; // 中文语言包
import en from './locales/en';

// ✅ 必须在 createI18n 之前读取
const savedLocale = localStorage.getItem('locale') || 'zh'

const i18n = createI18n({
    locale: savedLocale, // 默认语言为中文
    messages: {
        zh: { ...zh },
        en: { ...en},
    },
    legacy: false,
    globalInjection: true

});

export default i18n;