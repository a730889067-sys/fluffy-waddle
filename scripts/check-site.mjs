import fs from "node:fs";
const required = ["index.html","styles.css","app.js","manifest.webmanifest","sw.js","icon.svg","README_AR.md"];
for (const file of required) if (!fs.existsSync(file)) throw new Error("Missing required file: " + file);
const html = fs.readFileSync("index.html","utf8");
for (const asset of ["./styles.css","./app.js","./manifest.webmanifest","./icon.svg"]) {
  if (!html.includes(asset)) throw new Error("index.html does not reference " + asset);
}
const manifest = JSON.parse(fs.readFileSync("manifest.webmanifest","utf8"));
if (!manifest.name || !manifest.start_url || !Array.isArray(manifest.icons) || !manifest.icons.length) throw new Error("Invalid web app manifest");
const sw = fs.readFileSync("sw.js","utf8");
for (const asset of ["./index.html","./styles.css","./app.js","./manifest.webmanifest","./icon.svg"]) {
  if (!sw.includes(asset)) throw new Error("Service worker cache list is missing " + asset);
}
console.log("TESTCRAFT static checks passed: required assets, HTML references, manifest and offline cache.");
