#Requires -Version 7.0
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$script:ProjectRoot = Split-Path $PSScriptRoot -Parent

function Get-ModVersion {
    $line = @(Get-Content -LiteralPath (Join-Path $script:ProjectRoot 'gradle.properties') | Where-Object { $_ -match '^mod_version=' })
    if ($line.Count -ne 1) { throw 'Expected one mod_version in gradle.properties.' }
    $version = $line[0].Substring('mod_version='.Length).Trim()
    if ($version -notmatch '^\d+\.\d+\.\d+(?:-[A-Za-z0-9.-]+)?$') { throw "Invalid mod_version: $version" }
    return $version
}

function Get-BuildMatrix {
    $entries = @(Get-Content -LiteralPath (Join-Path $script:ProjectRoot 'versions.json') -Raw | ConvertFrom-Json)
    if (!$entries.Count) { throw 'Empty version matrix.' }
    $seen = [Collections.Generic.HashSet[string]]::new()
    foreach ($entry in $entries) {
        if ($entry.minecraft -notmatch '^\d+(?:\.\d+){1,2}$' -or !$seen.Add($entry.minecraft)) { throw 'Invalid or duplicate Minecraft version.' }
        if ($entry.fabric -notmatch '^[0-9A-Za-z.+-]+$' -or $entry.modmenu -notmatch '^[0-9A-Za-z.+-]+$') { throw 'Invalid dependency version.' }
        $modern = $entry.minecraft.StartsWith('26.')
        [pscustomobject]@{
            minecraft = $entry.minecraft
            fabric = $entry.fabric
            modmenu = $entry.modmenu
            loader = $(if ($modern) { '0.20.1' } else { '0.18.4' })
            modern = $modern
            java = $(if ($modern) { 25 } else { 21 })
        }
    }
}
