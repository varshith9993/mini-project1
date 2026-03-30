$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$outDir = Join-Path $root "out"

if (Test-Path $outDir) {
    Remove-Item -Recurse -Force $outDir
}

New-Item -ItemType Directory -Path $outDir | Out-Null

$sources = Get-ChildItem -Path (Join-Path $root "src") -Recurse -Filter *.java |
    Select-Object -ExpandProperty FullName

if (-not $sources) {
    throw "No Java source files were found under src."
}

javac --release 17 -d $outDir $sources
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed."
}

java -cp $outDir aadhaar.app.AadhaarDbtAwarenessApp
