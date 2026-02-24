import { useColorMode } from '@vueuse/core';
import { computed } from 'vue';

export const useDark = () => {
    const mode = useColorMode({
        emitAuto: true,
        storageKey: "ACTIVE_COLOR_SCHEME",
        disableTransition: false,
        initialValue: 'light'
    });

    return {
        isDark: computed(
            () =>
                (mode.store.value === 'auto' ? mode.system.value : mode.store.value) ===
                'dark'
        ),
        mode
    };
};