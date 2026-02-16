import { createI18n } from 'vue-i18n';
import zh from './locales/zh'; // 中文语言包
import en from './locales/en';
import zhCn from 'element-plus/es/locale/lang/zh-cn';
import enUs from 'element-plus/es/locale/lang/en';

const i18n = createI18n({
    locale: 'zh', // 默认语言为中文
    messages: {
        zh: { ...zh, ...zhCn },  // 合并 element-plus 中文包
        en: { ...en, ...enUs },  // 合并 element-plus 英文包
    },
    legacy: false,
    globalInjection: true
});

export default i18n;
