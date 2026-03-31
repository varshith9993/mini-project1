# Aadhaar DBT Awareness System

A Java-only website application that explains the difference between:

- Aadhaar linked bank accounts
- DBT-enabled Aadhaar seeded bank accounts

The project uses:

- Java's built-in `HttpServer` for routing and page delivery
- Server-rendered HTML/CSS generated from Java classes for the frontend
- Plain Java service classes for the backend/content layer
- JDK 17+ compatible source (`javac --release 17`)

## Modules

- Interactive user interface
- Information and comparison module
- Multilingual support
- Video learning module
- Government resources module
- Quiz and awareness module

## Branding

The UI theme follows the provided reference image and reuses extracted image assets from it:

- `assets/aadhaar-logo.png`
- `assets/learn-panel.png`
- `assets/reference-ui.jpg`

## Run

From PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\run.ps1
```

The script compiles the source into `out/`, starts the local server on `http://localhost:8080/`, and opens the site in your browser.

## Compile only

```powershell
$sources = Get-ChildItem -Path .\src -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
javac --release 17 -encoding UTF-8 -d out $sources
```

## Notes

- Official references inside the app were verified from UIDAI, NPCI, and DBT Bharat pages on 30 March 2026.
- The UI is styled to closely follow the provided reference image, including the browser frame, blue navigation strip, phase cards, and learn-panel layout.
- All modules are delivered by plain Java without external dependencies.
