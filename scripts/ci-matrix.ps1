#Requires -Version 7.0
. (Join-Path $PSScriptRoot 'common.ps1')
$matrix = @{ include = @(Get-BuildMatrix) } | ConvertTo-Json -Depth 5 -Compress
$version = Get-ModVersion
if ($env:GITHUB_OUTPUT) {
    Add-Content -LiteralPath $env:GITHUB_OUTPUT -Value "matrix=$matrix" -Encoding utf8
    Add-Content -LiteralPath $env:GITHUB_OUTPUT -Value "version=$version" -Encoding utf8
} else {
    Write-Output $matrix
}
