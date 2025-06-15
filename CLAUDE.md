# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

yakushima-attendance-app is an attendance management system (勤怠管理システム) for Yakushima Town Hall (屋久島町役場). It's a Node.js/TypeScript backend with PostgreSQL database using Prisma ORM, designed for employee attendance and work hour management.

## Essential Commands

### Development Setup
```bash
# Start PostgreSQL database
docker-compose up -d

# Install dependencies (run in /backend)
npm install

# Generate Prisma client
npm run prisma:generate

# Run database migrations
npm run prisma:migrate

# Seed initial data
npm run prisma:seed

# Start development server
npm run dev
```

### Code Quality & Testing
```bash
npm run lint           # Check code quality
npm run lint:fix       # Auto-fix linting issues
npm test              # Run tests
npm run test:coverage # Run tests with coverage
```

### Database Operations
```bash
npm run prisma:studio    # Open database GUI
npm run prisma:deploy    # Deploy migrations (production)
```

## Architecture

### Tech Stack
- **Backend**: Node.js + Express.js + TypeScript
- **Database**: PostgreSQL + Prisma ORM
- **Auth**: JWT + bcrypt
- **Logging**: Winston with file rotation
- **Testing**: Jest + Supertest

### Database Schema
The application manages:
- **Users (職員マスタ)**: Employee records with authentication
- **Departments (部署マスタ)**: Hierarchical department structure
- **Positions (役職マスタ)**: Employee positions with levels
- **Roles (ロールマスタ)**: Permission-based role system with JSON permissions
- **UserRoles**: Many-to-many user-role relationships

### Directory Structure
```
backend/src/
├── config/     # Database configuration
├── types/      # TypeScript type definitions
├── utils/      # Authentication & logging utilities
├── routes/     # API route handlers
├── services/   # Business logic services
└── middleware/ # Express middleware
```

### Key Configuration
- **Database Port**: 5433 (mapped from Docker container)
- **TypeScript**: ES2020 target, path aliases `@/*` → `src/*`
- **Environment**: Expects `DATABASE_URL`, `JWT_SECRET`, `NODE_ENV`

### Test Users (from seed data)
- Admin: Employee ID `0001`, Password `password123`  
- Employee: Employee ID `1001`, Password `password123`

## Development Notes

### Current Status
- ✅ Database schema, migrations, and seeding complete
- ✅ Authentication utilities and logging infrastructure ready
- ✅ TypeScript types and development tooling configured
- 🔄 API routes, services, and middleware need implementation
- 🔄 Frontend application not yet started

### Important Files
- `prisma/schema.prisma`: Comprehensive database schema with Japanese comments
- `src/utils/auth.ts`: JWT and password hashing utilities
- `src/utils/logger.ts`: Winston logging with audit capabilities
- `src/types/index.ts`: Complete TypeScript type definitions