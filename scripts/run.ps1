$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$buildStamp = Get-Date -Format "yyyyMMdd-HHmmss"
$outDir = Join-Path $root ("out\\build-" + $buildStamp)

New-Item -ItemType Directory -Path $outDir -Force | Out-Null

$sources = Get-ChildItem -Path (Join-Path $root "src") -Recurse -Filter *.java |
    Select-Object -ExpandProperty FullName

if (-not $sources) {
    throw "No Java source files were found under src."
}

javac --release 17 -encoding UTF-8 -d $outDir $sources
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed."
}

java -cp $outDir aadhaar.app.AadhaarDbtAwarenessApp
