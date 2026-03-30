# Aadhaar DBT Awareness System

A Java-only desktop application that explains the difference between:

- Aadhaar linked bank accounts
- DBT-enabled Aadhaar seeded bank accounts

The project uses:

- Java Swing for the frontend
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

The script compiles the source into `out/` and launches the desktop app.

## Compile only

```powershell
$sources = Get-ChildItem -Path .\src -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
javac --release 17 -d out $sources
```

## Notes

- Official references inside the app were verified from UIDAI, NPCI, and DBT Bharat pages on 30 March 2026.
- Video actions open the target pages in the system browser, which keeps the application dependency-free and fully Java-based.
