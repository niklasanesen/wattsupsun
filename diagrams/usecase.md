

```mermaid

graph TD
    Homeowner[Homeowner] --> A[Enter address and roof data]
    A --> B[Calculate solar panel potential]
    StromprisAPI[StromprisAPI] --> B
    MetDataAPI[MetDataAPI] --> B
    B --> C[Estimate annual electricity production]
    B --> D[Estimate economic savings]
    B --> E[Estimate number of coffee cups]
    Homeowner --> F[View analysis results]
    F --> C
    F --> D
    F --> E