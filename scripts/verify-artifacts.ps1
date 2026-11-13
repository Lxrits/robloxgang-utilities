#Requires -Version 7.0
param([string]$Directory = 'dist', [string]$MinecraftVersion)
. (Join-Path $PSScriptRoot 'common.ps1')
Add-Type -AssemblyName System.IO.Compression.FileSystem
$directoryPath = (Resolve-Path -LiteralPath $Directory).Path
$version = Get-ModVersion
$entries = @(Get-BuildMatrix)
if ($MinecraftVersion) {
    $entries = @($entries | Where-Object { $_.minecraft -ceq $MinecraftVersion })
    if ($entries.Count -ne 1) { throw "Unsupported Minecraft version: $MinecraftVersion" }
}
$expectedNames = @($entries | ForEach-Object { "robloxgang-utilities-$version+$($_.minecraft).jar" })
if (!$MinecraftVersion) {
    $actualNames = @(Get-ChildItem -LiteralPath $directoryPath -Filter '*.jar' -File | ForEach-Object Name)
    if (@(Compare-Object $expectedNames $actualNames).Count) { throw 'Release directory must contain exactly one JAR for each version in versions.json.' }
}
$sums = foreach ($entry in $entries) {
    $name = "robloxgang-utilities-$version+$($entry.minecraft).jar"
    $path = Join-Path $directoryPath $name
    $archive = [IO.Compression.ZipFile]::OpenRead($path)
    try {
        $reader = [IO.StreamReader]::new($archive.GetEntry('fabric.mod.json').Open())
        try { $metadata = $reader.ReadToEnd() | ConvertFrom-Json } finally { $reader.Dispose() }
        if ($metadata.id -ne 'robloxgang_utilities' -or $metadata.version -cne "$version+$($entry.minecraft)" -or $metadata.depends.minecraft -cne $entry.minecraft -or $metadata.environment -ne 'client') { throw "Invalid mod metadata: $name" }
        if ($metadata.depends.java -ne ">=$($entry.java)" -or $metadata.depends.fabricloader -ne ">=$($entry.loader)") { throw "Invalid runtime dependencies: $name" }
        foreach ($photo in @('gang', 'chat', 'sneak', 'reminder', 'barriers', 'intro', 'overdose', 'anthem', 'blur42', 'reminder42')) {
            $png = $archive.GetEntry("assets/robloxgang_utilities/textures/demo/$photo.png")
            if (!$png -or $png.Length -lt 1000) { throw "Missing screenshot $photo in $name" }
            $imageStream = $png.Open()
            try {
                $pngHeader = [byte[]]::new(24); $offset = 0
                while ($offset -lt 24) {
                    $chunk = $imageStream.Read($pngHeader, $offset, 24 - $offset)
                    if ($chunk -eq 0) { throw "Truncated screenshot: $photo in $name" }
                    $offset += $chunk
                }
            } finally { $imageStream.Dispose() }
            $signature = [BitConverter]::ToString($pngHeader, 0, 8).Replace('-', '')
            $imageWidth = $pngHeader[16] * 16777216 + $pngHeader[17] * 65536 + $pngHeader[18] * 256 + $pngHeader[19]
            $imageHeight = $pngHeader[20] * 16777216 + $pngHeader[21] * 65536 + $pngHeader[22] * 256 + $pngHeader[23]
            if ($signature -ne '89504E470D0A1A0A' -or $imageWidth -ne 1280 -or $imageHeight -ne 720) { throw "Invalid screenshot format/dimensions: $photo in $name" }
        }
        foreach ($required in @('LICENSE', 'MEDIA-LICENSES.md', 'assets/robloxgang_utilities/intro/page_0.png', 'assets/robloxgang_utilities/intro/page_41.png', 'assets/robloxgang_utilities/intro/audio.wav', 'assets/robloxgang_utilities/sounds/anthem.ogg', 'assets/robloxgang_utilities/sounds/siren.ogg', 'assets/robloxgang_utilities/font/blur.json', 'robloxgang_utilities.mixins.json', 'assets/robloxgang_utilities/icon.png')) { if (!$archive.GetEntry($required)) { throw "Missing $required in $name" } }
        if (@($archive.Entries | Where-Object { $_.FullName -like 'net/minecraft/*' -or $_.Name -like '*Smoke*' -or $_.Name -in @('ServerCapture.class','UpdateCapture.class') }).Count) { throw "Development/game classes found in $name" }
        $stream = $archive.GetEntry('ru/robloxgang/utilities/RobloxGangUtilities.class').Open()
        try { $header = [byte[]]::new(8); $read = $stream.Read($header, 0, 8) } finally { $stream.Dispose() }
        if ($read -ne 8 -or ($header[6] * 256 + $header[7]) -ne ($entry.java + 44)) { throw "Invalid Java bytecode target: $name" }
    } finally { $archive.Dispose() }
    Write-Host "Verified $name"
    "$((Get-FileHash -LiteralPath $path -Algorithm SHA256).Hash.ToLowerInvariant())  $name"
}
if (!$MinecraftVersion) { $sums | Set-Content -LiteralPath (Join-Path $directoryPath 'SHA256SUMS.txt') -Encoding utf8NoBOM }
