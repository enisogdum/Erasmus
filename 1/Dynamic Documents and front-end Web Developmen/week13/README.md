# Angular Project

The aim of the task is to learn the fundamentals of the Angular framework, a popular advanced front-end solution used to create web projects.

## Requirements

The task requires setting up a work environment that includes the Angular runtime environment, along with shell tools. It is recommended to use an IDE for working with the code:

- VS Code - https://code.visualstudio.com/docs/nodejs/angular-tutorial
- JetBrains's IDE:
  - PHPStorm - https://www.jetbrains.com/help/phpstorm/angular.html
  - IntelliJ - https://www.jetbrains.com/help/idea/angular.html

All IDEs are available via RDP or can be freely downloaded (Student licence is required for JetBrain's products - can be obtained online).

## Before you start the project

There are 3 key elements to understand before starting the project.

The first one is the core work environment for Angular. Node.js runs the Angular CLI, build tools, package managers (npm/yarn/pnpm), and the local dev server (`ng serve`). It's not required in the browser runtime, i.e., it only powers the developer toolchain.

Angular applications are written in TypeScript (an extension of JavaScript). TypeScript provides static typing, generics, interfaces, and IDE tooling that improve developer productivity, catch errors at compile time, and enable better refactoring and autocomplete for Angular constructs. TypeScript source (`.ts`) is transpiled to ECMAScript JavaScript by the TypeScript compiler (`tsc`) as part of the Angular build.

The most essential development tool is the Angular CLI (`ng` command). It is the official command-line tool for creating, developing, building, and testing Angular applications. It scaffolds projects and components with best-practice structure, runs a local dev server with live reload (`ng serve`), generates code (`ng generate`), builds optimised production artefacts (`ng build`), and integrates testing and add-on configuration (`ng test`, `ng e2e`, `ng add`). It automates routine tasks, enforces consistency, and is extensible via schematics and builders.

A detailed explanation of Angular CLI is available at: https://angular.dev/tools/cli

### Common Angular CLI commands

- `ng new` — create a new app
- `ng serve` — run dev server (localhost:4200)
- `ng generate component|service|module` (alias: `ng g`) — scaffold code
- `ng build [--prod]` — compile app for deployment
- `ng test` — run unit tests
- `ng e2e` — run end-to-end tests
- `ng lint` — run linter
- `ng add` — install & configure libraries (e.g., `ng add @angular/material`)
- `ng update` — update Angular and dependencies

For detailed information see: https://angular.dev/cli

## Project preparation

After preparing the IDE (please pay attention to the recommended plugins in the documentation), you should (if for any reason you don't have it - it was required for past exercises):

- Install Node.js (recommended LTS version – currently 24). On computers with admin access, install Node.js using the installer for your operating system. If you don't have admin access (e.g., on WI terminals), download Node.js as a ZIP archive [nodejs (.zip) for Windows 64-bit], extract it, and add it to the PATH environment variable for the account.
- For other configurations or operating systems, you can use the package manager available in the OS.
- After installing/extracting Node.js, add the npm path to the shell PATH.
- Verify the correctness of the installation and configuration by using `npm -version` in the shell.

The actual initial step to lay a foundation for the Angular project requires adding it to Node.js, which can be done simply by running Angular CLI with:

```
npm install -g @angular/cli
```

Read material about an exemplary application at https://angular.dev/tutorials/first-app. Pay attention to the sections from Introduction to Angular Services.

To continue with this task, it is necessary to create a new project. We create it using the CLI:

```
ng new projectName
```

(for all questions, respond with N for the default value). After entering the project directory with `cd projectName`, we start it using `ng serve` (please remember this server is suitable for development purposes, not the production stage). The command `ng build` is used for distribution and requires files to be hosted by an actual production-ready HTTP server.

Those working via RDP should start the project using a non-standard port, e.g., `ng serve --port=12345`, and since all users work on a single machine, please remember to choose a random port.

Once the project is running, check that it is functioning correctly by opening a browser and entering the address of the running application. The project started this way operates in live-reload mode, and no restart is required after changes to project files.

## The task

Your task is to create an Angular component that renders a password input with:

- Real-time password strength evaluation.
- A strength label (e.g., Weak / Fair / Good / Strong) with colour that changes based on strength.
- Optional textual comment/suggestions (e.g., "add a symbol", "longer password").
- A toggle button to show/hide the password.

To allow you to focus on technology, not investigate logic in depth, the process of evaluating password strength doesn't have to be made in a very complex way. In short — try to catch basic issues, e.g.:

- too short password
- containing only characters (no symbols)
- repeated characters/symbols

**Note:** many useful string-related tasks can be easily done with regex patterns (e.g. repeated characters can be detected by `/(.)\1/`). This applies to all popular programming languages.

## Hints

The generator, when creating a project, will generate one basic component. Its sources are located in `src/app`: `app.component.ts` (the TypeScript class of the component), `app.component.html` (its view), `app.component.css` (styles for the view), and `app.component.spec.ts` (optional unit tests).

Using Angular CLI, we add components to the project:

```
ng generate component [Name]
```

Refer to: https://angular.dev/cli/generate/component and https://angular.dev/guide/components.

**Note:** In the latest versions of Angular, the recommended and default way to create components is standalone (the description is found in the first of the links in this section). Materials prepared for or generated based on older versions may assume the use of NgModule (https://angular.dev/api/core/NgModule).

For more complex tasks, a component typically relies on a service class that implements its logic. Using Angular CLI, we add a service class named `PasswordEvaluator` with:

```
ng generate service [Name]
```

See: https://angular.dev/guide/di/creating-and-using-services.

In the service class, we create the logic needed to evaluate the password provided as a parameter to the function. The service functionality requires methods that process the password and return information necessary for changing the colour and displaying text related to password issues. The `PasswordEvaluator` service is "injected" into the component — see https://angular.dev/guide/di.

## Report

At the end, prepare a report including:

- the source code of the prepared component,
- the source code of the service,
- screenshots showing the functionality for showing/hiding the password and all scenarios related to password issues.

Include the above elements in the report and upload it as a PDF here.
