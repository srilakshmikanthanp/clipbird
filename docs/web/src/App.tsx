import FeatureList from './components/FeatureList';
import Footer from './components/Footer';
import GettingStarted from './components/GettingStarted';
import Header from './components/Header';
import Introduction from './components/Introduction';

export default function App() {
  return (
    <div className="mx-auto flex min-h-screen max-w-6xl flex-col px-4 md:px-8">
      <Header />

      <main className="flex flex-1 flex-col gap-12 py-6 md:justify-center">
        <div className="grid items-center gap-10 md:grid-cols-2 md:gap-16">
          <Introduction />
          <FeatureList />
        </div>
        <GettingStarted />
      </main>

      <Footer />
    </div>
  );
}
