#Requires -Version 7.0
param([string]$MinecraftVersion)
. (Join-Path $PSScriptRoot 'scripts/common.ps1')
$entries = @(Get-BuildMatrix)
if ($MinecraftVersion) {
    $entries = @($entries | Where-Object { $_.minecraft -ceq $MinecraftVersion })
    if ($entries.Count -ne 1) { throw "Unsupported Minecraft version: $MinecraftVersion" }
}
foreach ($entry in $entries) { & (Join-Path $PSScriptRoot 'scripts/build-one.ps1') -MinecraftVersion $entry.minecraft }
if (!$MinecraftVersion) { & (Join-Path $PSScriptRoot 'scripts/verify-artifacts.ps1') -Directory (Join-Path $PSScriptRoot "dist/$(Get-ModVersion)") }
