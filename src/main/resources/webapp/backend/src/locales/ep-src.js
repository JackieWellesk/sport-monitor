const Hle = Ne(Vle, [["render", Tle]])
    , Ile = z({
    inheritAttrs: !1,
    __name: "vp-theme-toggler",
    setup(e) {
        const t = P(Nn.value)
            , n = P();
        ie( () => Nn.value, o => {
                t.value = o
            }
        ),
            ie( () => t.value, o => {
                    o !== Nn.value && cre()
                }
            );
        const r = () => new Promise(o => {
                var C;
                if (!(document.startViewTransition && !window.matchMedia("(prefers-reduced-motion: reduce)").matches)) {
                    o(!0);
                    return
                }
                const i = ((C = n.value) == null ? void 0 : C.$el).getBoundingClientRect()
                    , u = i.left + i.width / 2
                    , d = i.top + i.height / 2
                    , p = Math.hypot(Math.max(u, innerWidth - u), Math.max(d, innerHeight - d))
                    , f = 100 * u / innerWidth
                    , v = 100 * d / innerHeight
                    , m = Math.hypot(innerWidth, innerHeight) / Math.SQRT2
                    , g = 100 * p / m;
                document.startViewTransition(async () => {
                        o(!0),
                            await Me()
                    }
                ).ready.then( () => {
                        const y = [`circle(0% at ${f}% ${v}%)`, `circle(${g}% at ${f}% ${v}%)`];
                        document.documentElement.animate({
                            clipPath: Nn.value ? [...y].reverse() : y
                        }, {
                            duration: 400,
                            easing: "ease-in",
                            fill: "both",
                            pseudoElement: Nn.value ? "::view-transition-old(root)" : "::view-transition-new(root)"
                        })
                    }
                )
            }
        );
        return (o, l) => {
            const s = et("el-switch")
                , i = et("ClientOnly");
            return b(),
                ne(i, null, {
                    default: Y( () => [Z(s, mt({
                        ref_key: "switchRef",
                        ref: n,
                        modelValue: t.value,
                        "onUpdate:modelValue": l[0] || (l[0] = u => t.value = u)
                    }, o.$attrs, {
                        "before-change": r,
                        "active-action-icon": $le,
                        "inactive-action-icon": Hle
                    }), null, 16, ["modelValue"])]),
                    _: 1
                })
        }
    }
})
    , Rm = Ne(Ile, [["__scopeId", "data-v-a835bad3"]])
    , Ble = z({
    __name: "vp-theme-toggler",
    setup(e) {
        const t = N6();
        return (n, r) => (b(),
            E("div", {
                class: "theme-toggler-content"
            }, [Z(Rm, {
                "aria-label": c(t)["theme-toggler"],
                "aria-checked": c(Nn)
            }, null, 8, ["aria-label", "aria-checked"])]))
    }
})
    , Ale = Ne(Ble, [["__scopeId", "data-v-02ddc817"]])
    , Ple = {
    viewBox: "0 0 24 24",
    width: "1.2em",
    height: "1.2em"
};