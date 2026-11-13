#Requires -Version 7.0
param([Parameter(Mandatory)][string]$MinecraftVersion, [switch]$DryRun)
. (Join-Path $PSScriptRoot 'common.ps1')
$selectedEntries = @(Get-BuildMatrix | Where-Object { $_.minecraft -ceq $MinecraftVersion })
if ($selectedEntries.Count -ne 1) { throw "Unsupported Minecraft version: $MinecraftVersion" }
$entry = $selectedEntries[0]
$version = Get-ModVersion
$argsForGradle = @("-Pminecraft_version=$($entry.minecraft)", "-Pfabric_version=$($entry.fabric)", "-Pmodmenu_version=$($entry.modmenu)", "-Ploader_version=$($entry.loader)", '--project-cache-dir', ".gradle/variants/$MinecraftVersion", 'build', '--no-daemon', '--max-workers=2')
if ($entry.modern) { $argsForGradle += '-Pmodern=true' }
$jar = "robloxgang-utilities-$version+$MinecraftVersion.jar"
if ($DryRun) {
    [pscustomobject]@{ minecraft = $MinecraftVersion; modVersion = $version; arguments = $argsForGradle; output = "dist/$version/$jar" }
    return
}
Push-Location $script:ProjectRoot
try {
    $wrapper = if ($IsWindows) { '.\gradlew.bat' } else { './gradlew' }
    & $wrapper @argsForGradle
    if ($LASTEXITCODE -ne 0) { throw "Gradle build failed: $MinecraftVersion" }
    New-Item -ItemType Directory -Force "dist/$version" | Out-Null
    Copy-Item -LiteralPath "build/$MinecraftVersion/libs/$jar" -Destination "dist/$version"
    & (Join-Path $PSScriptRoot 'verify-artifacts.ps1') -Directory "dist/$version" -MinecraftVersion $MinecraftVersion
} finally { Pop-Location }
