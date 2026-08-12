# The Aurora SDK emulator on an Apple Silicon Mac

Split out of [AURORA.md](AURORA.md) because it is about one machine's SDK installation rather than
about the app: nothing here changes if the tablet code changes, and nobody on an Intel Mac or on
Linux needs any of it. What it is for is the emulator described in AURORA.md's Setup and Commands —
on an M-series Mac that emulator does not start until this is done.

Out of the box it does not start, and the IDE misreports why: it says `Failed to start virtual
machine … waiting for virtual machine started is timed out`, as if the VM were slow. It is not —
qemu dies in the first second. `QT_LOGGING_RULES="sfdk.*=true" sfdk emulator start …` shows the
real error:

```
qemu-system-x86_64: -accel hvf: Error: ret = [unknown hv_return value] (0x4, hvf-accel-ops.c:328)
```

libsfdk hardcodes `-accel hvf -cpu host` (UTF-16 literals inside `libSfdk.*.dylib`; there is no
setting for it), and Hypervisor.framework on ARM cannot virtualise an x86_64 guest at all. The
qemu binary is itself x86_64 running under Rosetta, which is fine — the accelerator is not.

The way through is software emulation. Rename the real binary and put a wrapper in its place that
rewrites the two arguments, keeping the pid by `exec`ing so `sfdk` still finds the process:

```sh
# ~/AuroraOS/share/qemu/bin/qemu-system-x86_64  (the real one moved to .real alongside)
#!/bin/sh
for arg in "$@"; do
    case "$arg" in
        hvf)  set -- "$@" tcg ;;
        host) set -- "$@" max ;;
        *)    set -- "$@" "$arg" ;;
    esac
    shift
done
exec "$(dirname "$0")/qemu-system-x86_64.real" "$@"
```

Under TCG the guest boots in about a minute — `sfdk` rides out its own ssh timeouts and connects —
and qemu settles to a few percent CPU once idle. Reinstalling or updating the SDK removes the
wrapper, and the symptom comes back looking like a timeout again.

Docker is unaffected by any of this: `aurora-build-tools` is a `linux/amd64` image and Docker runs
it through Rosetta in user mode, where the kernel stays native. That is why builds work on a
machine whose emulator does not. An empty `buildengines.xml` and `Failed to load system-wide build
engine configuration` in the `sfdk` log are normal for this SDK flavour — the build goes through
the Docker wrappers in `~/AuroraOS/sdk/<version>/tools/`, not through a VM build engine.
