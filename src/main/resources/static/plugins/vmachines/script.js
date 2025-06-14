function initPluginVmachines() {
  const osList = [
    "Windows 11", "Windows 10", "Windows Server 2022", "Windows Server 2019",
    "Ubuntu 24.04 LTS", "Ubuntu 22.04 LTS", "Ubuntu 20.04 LTS",
    "Debian 12", "Debian 11", "Fedora 41", "Fedora 40",
    "RHEL 9", "CentOS Stream 9", "AlmaLinux 9", "SUSE SLES 15",
    "Oracle Linux 9", "FreeBSD 13",
    "Arch Linux (latest ISO)", "EndeavourOS", "Artix Linux", "Manjaro (Arch-based)",
    "BlackArch"
  ];

  const root = document.getElementById('vmachines-metrics');
  if (!root) {
    console.warn("vmachines plugin root not found");
    return;
  }

  const osSelect = root.querySelector('select[name=osType]');
  if (!osSelect) {
    console.warn("OS select field not found");
    return;
  }

  osList.forEach(os => osSelect.add(new Option(os, os)));

  osSelect.addEventListener('change', (e) => {
    const { disk, ram } = vmachinesGuessDefaults(e.target.value);
    root.querySelector('input[name=diskSize]').value = disk;
    root.querySelector('input[name=memSize]').value = ram;
  });

  root.querySelector('#vm-create-form')
    .addEventListener('submit', vmachinesHandleSubmit);
}

function deinitPluginVmachines() {
  const form = document.getElementById('vm-create-form');
  form?.removeEventListener('submit', vmachinesHandleSubmit);
}

function vmachinesGuessDefaults(os) {
  const lower = os.toLowerCase();
  if (lower.includes("windows server")) return { disk: 80, ram: 6 };
  if (lower.includes("windows")) return { disk: 64, ram: 4 };
  if (lower.includes("ubuntu") || lower.includes("debian")) return { disk: 20, ram: 2 };
  if (lower.includes("fedora") || lower.includes("rhel") || lower.includes("centos")) return { disk: 30, ram: 2 };
  if (lower.includes("arch") || lower.includes("manjaro") || lower.includes("endeavour")) return { disk: 15, ram: 1.5 };
  if (lower.includes("freebsd")) return { disk: 10, ram: 1 };
  return { disk: 20, ram: 2 };
}

function vmachinesHandleSubmit(e) {
  e.preventDefault();
  const form = e.target;
  const data = Object.fromEntries(new FormData(form));

  if (!data.iso || !(data.iso.startsWith("/") || data.iso.startsWith("https://"))) {
    alert("ISO path must start with `/` or `https://`.");
    return;
  }

  console.log("vmachines create:", data);

  document.getElementById('vm-status').textContent =
    `Creating VM "${data.name}" with ${data.diskSize}GB / ${data.memSize}GB...`;
}
