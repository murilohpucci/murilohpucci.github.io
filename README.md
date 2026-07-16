# murilopucci.dev

Personal blog/site built with [Cryogen](http://cryogenweb.org/), a static site generator written in Clojure. Content is authored in Markdown, compiled to static HTML, and deployed automatically to GitHub Pages via GitHub Actions.

## Requirements

- **Java (JDK) 21** — [Temurin](https://adoptium.net/) recommended
- **Clojure CLI tools** (`clojure`/`clj`) — [installation guide](https://clojure.org/guides/install_clojure)
- Git

No other local dependencies are required — the Clojure CLI resolves everything declared in `deps.edn` on first run.

## Project structure

```
.
├── content/          # Markdown posts and pages
├── themes/           # Site theme (HTML templates, CSS, assets)
├── public/           # Generated output (git-ignored, built on demand)
├── deps.edn          # Project dependencies and aliases
├── config.edn        # Cryogen site configuration
├── src/
│   └── cryogen/
│       └── server.clj  # Local dev server (compiles + serves + watches files)
|   └── image_resizer/
│       └── cli.clj  # Local CLI tool to automatically convert the images to webp
└── .github/
    └── workflows/
        └── deploy.yml   # CI/CD: builds the site and deploys to GitHub Pages
```

## Running locally

Start a local development server with live rebuild on file changes:

```bash
clojure -X:serve
```

This will:
1. Compile the site once (Markdown → HTML into `public/`)
2. Start a local server at `http://localhost:3000`
3. Watch `content/` and `themes/` for changes and recompile automatically
4. Open your default browser

### Fast mode (incremental builds)

For quicker feedback while writing, use the `:fast` alias — it recompiles only the files that changed instead of rebuilding everything, and enables auto-refresh in the browser:

```bash
clojure -X:fast
```

## Building for production

To generate the static site once (without starting a server), used by the CI/CD pipeline:

```bash
clojure -M:build
```

Output is written to the `public/` directory, ready to be published.

## Deployment

The site deploys automatically via **GitHub Actions** on every push to `main` (see `.github/workflows/deploy.yml`):

1. Checks out the repo
2. Sets up Java 21 and the Clojure CLI
3. Runs `clojure -M:build` to generate `public/`
4. Uploads `public/` as a Pages artifact and deploys it via GitHub Pages

You can also trigger a deploy manually from the **Actions** tab (`workflow_dispatch`).

## Writing content

Add new posts as Markdown files under `content/md/posts/` (or the path configured in `config.edn`), following Cryogen's [front-matter conventions](https://cryogenweb.org/docs/writing-posts.html) (title, date, tags, etc.).

## Image optimization
 
The `image-resizer` CLI (in `src/image_resizer/cli.clj`) converts images to **WebP** format before you commit them, to keep the repository and the published site lightweight.
 
### Requirements
 
- [ImageMagick](https://imagemagick.org/) must be installed and available on your `PATH` (it shells out to the `convert` command).
Install it with:
 
```bash
# macOS
brew install imagemagick
 
# Ubuntu/Debian
sudo apt install imagemagick
```
 
### What it does
 
The script scans a folder for `.png`, `.jpeg`, and `.jpg` files, converts each one to `.webp` (stripping metadata and applying the given quality), and **deletes the original file** once the conversion succeeds.
 
### Usage
 
```bash
clojure -M -m image-resizer.cli [options]
```
 
**Options:**
 
| Flag | Default | Description |
|------|---------|-------------|
| `-p, --path PATH` | `content/img` | Folder to scan for images (recursive glob) |
| `-q, --quality QUALITY` | `85` | WebP output quality (0–100) |
| `-h, --help` | | Show help |
 
**Examples:**
 
```bash
# Convert all images under content/img using the default quality (85)
clojure -M -m image-resizer.cli
 
# Convert images in a custom folder with higher quality
clojure -M -m image-resizer.cli --path content/img/posts --quality 95
```
 
> ⚠️ **Note:** originals are deleted automatically after a successful conversion. Make sure the source images are committed to Git (or backed up elsewhere) before running this, in case you need the originals again.