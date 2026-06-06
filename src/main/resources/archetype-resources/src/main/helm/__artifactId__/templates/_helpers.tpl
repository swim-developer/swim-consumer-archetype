{{- define "${artifactId}.labels" -}}
app: {{ .Values.appName }}
app.kubernetes.io/part-of: swim-${serviceName}
{{- end }}

{{- define "${artifactId}.selectorLabels" -}}
app: {{ .Values.appName }}
{{- end }}
